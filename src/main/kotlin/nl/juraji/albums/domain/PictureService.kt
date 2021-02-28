package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.nio.file.Path

@Service
class PictureService(
    private val directoryRepository: DirectoryRepository,
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations,
    private val publisher: ApplicationEventPublisher

) {
    fun getPicture(pictureId: String): Mono<Picture> = pictureRepository.findById(pictureId)

    fun getByDirectoryId(directoryId: String, pageable: Pageable): Flux<Picture> =
        pictureRepository.findPageByDirectoryId(directoryId, pageable)

    fun getImageLocationById(id: String): Mono<String> = pictureRepository.findImageLocationById(id)

    fun existsByLocation(location: String): Mono<Boolean> = pictureRepository.existsByLocation(location)

    fun addPicture(directoryId: String, location: String, name: String?): Mono<Picture> = Mono.just(location.toPath())
        .validateAsync { path ->
            isTrue(fileOperations.exists(path)) { "File with path $path does not exist on your system or it is not readable" }
            isFalse(pictureRepository.existsByLocation(path.toString())) { "File with path $path was already added" }
            isTrue(directoryRepository.existsById(directoryId)) { "No parent directory exists for $path by id $directoryId" }
        }
        .zipWith(directoryRepository.findById(directoryId))
        .flatMap { (path, parentDir) -> addPicture(parentDir, path, name) }

    fun addPicture(parentDirectory: Directory, location: Path, name: String?): Mono<Picture> = Mono.just(location)
        .map { path ->
            Picture(
                location = path.toString(),
                name = name ?: path.fileName.toString(),
                directory = parentDirectory
            )
        }
        .flatMap(pictureRepository::save)
        .doOnNext { publisher.publishEvent(PictureCreatedEvent(it.id!!, it.location, it.directory.id!!)) }

    fun deletePicture(pictureId: String, doDeleteFile: Boolean): Mono<Unit> = pictureRepository
        .findById(pictureId)
        .flatMap { pictureRepository.deleteTreeById(it.id!!).thenReturn(it) }
        .doOnNext { publisher.publishEvent(PictureDeletedEvent(it.id!!, it.location, doDeleteFile)) }
        .mapToUnit()

    fun addTag(pictureId: String, tagId: String): Mono<Unit> = pictureRepository
        .addTag(pictureId, tagId)
        .doOnNext { publisher.publishEvent(PictureUpdatedEvent(pictureId)) }

    fun removeTag(pictureId: String, tagId: String): Mono<Unit> = pictureRepository
        .removeTag(pictureId, tagId)
        .doOnNext { publisher.publishEvent(PictureUpdatedEvent(pictureId)) }
}
