package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.toPath
import nl.juraji.albums.util.toUnit
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PictureService(
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
        }
        .map { path -> Picture(location = path.toString(), name = name ?: path.fileName.toString()) }
        .flatMap(pictureRepository::save)
        .doOnNext { applicationEventPublisher.publishEvent(PictureCreatedEvent(this, it)) }

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
