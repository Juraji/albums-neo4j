package nl.juraji.albums.api

import nl.juraji.albums.model.Directory
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.Tag
import nl.juraji.albums.model.relationships.DirectoryContainsPicture
import nl.juraji.albums.model.relationships.PictureTaggedByTag
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.repositories.PictureRepository
import nl.juraji.albums.services.FileOperations
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class PictureService(
    private val directoryRepository: DirectoryRepository,
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations
) {
    fun getAllPictures(): Flux<Picture> = pictureRepository.findAll()

    fun getPicture(pictureId: String): Mono<Picture> = pictureRepository.findById(pictureId)

    fun addPicture(picture: Picture): Mono<Picture> = Mono.just(picture)
        .validateAsync {
            synchronous {
                isTrue(it.id == null) { "Id should be null for new entities" }
            }

            isTrue(fileOperations.exists(it.location.toPath())) { "File with path ${picture.location} does not exist on your system" }
            isFalse(pictureRepository.existsByLocation(it.location)) { "File with path ${picture.location} was already added" }
        }
        .flatMap(pictureRepository::save)
        .doOnNext(this::doAddPictureToDirectory)

    fun tagPictureBy(pictureId: String, tag: Tag): Mono<Picture> = Mono.just(tag)
        .validate {
            isNotNull(tag.id) { "You need to first save a tag before you can add it to a picture" }
        }
        .zipWith(pictureRepository.findById(pictureId))
        .map { (tag, picture) ->
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
            .subscribe()
    }
}
