package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.PictureTagsRepository
import nl.juraji.albums.domain.tags.Tag
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PictureTagsService(
    private val pictureTagsRepository: PictureTagsRepository
) {
    fun getPictureTags(pictureId: String): Flux<Tag> =
        pictureTagsRepository.findPictureTags(pictureId)

    fun addTagToPicture(pictureId: String, tagId: String): Mono<Tag> =
        pictureTagsRepository.addTagToPicture(pictureId, tagId)

    fun removeTagFromPicture(pictureId: String, tagId: String): Mono<Void> =
        pictureTagsRepository.removeTagFromPicture(pictureId, tagId)
}
