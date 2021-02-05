package nl.juraji.albums.domain

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
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.nio.file.Path

@Service
class PictureService(
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations,
    private val directoryService: DirectoryService
) {
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
        .doOnNext(directoryService::addPicture)

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
}
