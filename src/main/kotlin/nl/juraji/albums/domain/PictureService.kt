package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class PictureService(
    private val directoryRepository: DirectoryRepository,
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations,
    private val applicationEventPublisher: ApplicationEventPublisher

) {
    fun getAllPictures(): Flux<Picture> = pictureRepository.findAll()

    fun getPicture(pictureId: String): Mono<Picture> = pictureRepository.findById(pictureId)

    fun addPicture(location: String, name: String?): Mono<Picture> = Mono.just(location.toPath())
        .validateAsync { path ->
            isTrue(fileOperations.exists(path)) { "File with path $path does not exist on your system or it is not readable" }
            isFalse(pictureRepository.existsByLocation(path.toString())) { "File with path $path was already added" }
            isTrue(directoryRepository.existsByLocation(path.parent.toString())) { "No parent directory exists for $path" }
        }
        .zipWhen { directoryRepository.findByLocation(it.parent.toString()) }
        .map { (path, parentDir) ->
            Picture(
                location = path.toString(),
                name = name ?: path.fileName.toString(),
                directory = parentDir
            )
        }
        .flatMap(pictureRepository::save)
        .doOnNext { applicationEventPublisher.publishEvent(PictureCreatedEvent(it.id!!, it.location, it.directory.id!!)) }

    fun deletePicture(pictureId: String, doDeleteFile: Boolean): Mono<Unit> = pictureRepository
        .findById(pictureId)
        .flatMap { pictureRepository.deleteTreeById(it.id!!).thenReturn(it) }
        .doOnNext { applicationEventPublisher.publishEvent(PictureDeletedEvent(it.id!!, it.location, doDeleteFile)) }
        .mapToUnit()

    fun addTag(pictureId: String, tagId: String): Mono<Unit> = pictureRepository
        .addTag(pictureId, tagId)
        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId)) }

    fun removeTag(pictureId: String, tagId: String): Mono<Unit> = pictureRepository
        .removeTag(pictureId, tagId)
        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId)) }
}
