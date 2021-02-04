package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.toLocalDateTime
import nl.juraji.albums.util.toPath
import nl.juraji.albums.util.toUnit
import nl.juraji.reactor.validations.ValidationException
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
    private val fileOperations: FileOperations
) {
    private val addToDirectoryScheduler: Scheduler = Schedulers.newSingle("directory-rate-limiter")

    fun getAllPictures(): Flux<Picture> = pictureRepository.findAll()

    fun getPicture(pictureId: String): Mono<Picture> = pictureRepository.findById(pictureId)

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

    fun deletePicture(pictureId: String, deleteFile: Boolean? = false): Mono<Unit> =
        if (deleteFile == false) pictureRepository
            .deleteById(pictureId)
            .toUnit()
        else pictureRepository
            .findById(pictureId)
            .doOnNext { fileOperations.deleteIfExists(it.location.toPath()).subscribe() }
            .flatMap { pictureRepository.deleteById(it.id!!) }
            .toUnit()

    fun tagPictureBy(pictureId: String, tagId: String): Mono<Unit> =
        pictureRepository.addTag(pictureId, tagId).toUnit()

    fun removeTagFromPicture(pictureId: String, tagId: String): Mono<Unit> =
        pictureRepository.removeTag(pictureId, tagId).toUnit()

    private fun doAddPictureToDirectory(picture: Picture) {
        val parentLocation = picture.location.toPath().parent.toString()

        directoryRepository
            .findByLocation(parentLocation)
            .switchIfEmpty {
                Mono
                    .just(Directory(location = parentLocation))
                    .flatMap(directoryRepository::save)
            }
            .flatMap { directoryRepository.addPicture(it.id!!, picture.id!!) }
            .subscribeOn(addToDirectoryScheduler)
            .subscribe()
    }
}
