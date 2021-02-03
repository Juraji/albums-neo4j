package nl.juraji.albums.api

import nl.juraji.albums.model.*
import nl.juraji.albums.model.relationships.DirectoryContainsPicture
import nl.juraji.albums.model.relationships.PictureTaggedByTag
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.repositories.PictureRepository
import nl.juraji.albums.repositories.TagRepository
import nl.juraji.albums.services.FileOperations
import nl.juraji.albums.util.toLocalDateTime
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.ValidationException
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.nio.file.Path

@Service
class PictureService(
    private val directoryRepository: DirectoryRepository,
    private val pictureRepository: PictureRepository,
    private val tagRepository: TagRepository,
    private val fileOperations: FileOperations
) {
    private val addToDirectoryScheduler: Scheduler = Schedulers.newSingle("directory-rate-limiter")

    fun getAllPictures(): Flux<PictureDescription> = pictureRepository.findAllDescriptions()

    fun getPicture(pictureId: String): Mono<PictureDescription> = pictureRepository.findDescriptionById(pictureId)

    fun addPicture(location: String, name: String?): Mono<Picture> = Mono.just(location.toPath())
        .validateAsync { path ->
            isTrue(fileOperations.exists(path)) { "File with path $path does not exist on your system or it is not readable" }
            isFalse(pictureRepository.existsByLocation(path.toString())) { "File with path $path was already added" }
        }
        .map { path -> path to Picture(location = path.toString(), name = name ?: path.fileName.toString()) }
        .flatMap { (path, picture) -> this.enrichPictureMetaData(picture, path) }
        .flatMap(pictureRepository::save)
        .doOnNext(this::doAddPictureToDirectory)

    private fun enrichPictureMetaData(picture: Picture, path: Path): Mono<Picture> = Mono
        .zip(
            fileOperations
                .readContentType(path)
                .flatMap { FileType.of(it).toMono() }
                .switchIfEmpty { ValidationException("File type is not supported").toMono() },
            fileOperations
                .readAttributes(path)
        )
        .map { (fileType, fileAttrs) ->
            picture.copy(
                fileType = fileType,
                fileSize = fileAttrs.size(),
                lastModified = fileAttrs.lastModifiedTime().toLocalDateTime()
            )
        }

    fun tagPictureBy(pictureId: String, tagId: String): Mono<Picture> = Mono
        .zip(
            pictureRepository.findById(pictureId),
            tagRepository.findById(tagId)
        )
        .map { (picture, tag) ->
            val relationship = PictureTaggedByTag(tag)

            picture.copy(tags = picture.tags + relationship)
        }
        .flatMap(pictureRepository::save)

    private fun doAddPictureToDirectory(picture: Picture) {
        val parentLocation = fileOperations.getParentPathStr(picture.location)
        val relationship = DirectoryContainsPicture(picture)

        directoryRepository
            .findByLocation(parentLocation)
            .switchIfEmpty { Mono.just(Directory(location = parentLocation)) }
            .map { it.copy(pictures = it.pictures.plus(relationship)) }
            .flatMap(directoryRepository::save)
            .subscribeOn(addToDirectoryScheduler)
            .subscribe()
    }
}
