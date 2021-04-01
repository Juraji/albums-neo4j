package nl.juraji.albums.api

import nl.juraji.albums.domain.PictureTagsService
import nl.juraji.albums.domain.tags.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pictures/{pictureId}/tags")
class PictureTagsController(
    private val pictureTagsService: PictureTagsService
) {

    @GetMapping
    fun getPictureTags(
        @PathVariable pictureId: String
    ): Flux<Tag> = pictureTagsService.getPictureTags(pictureId)

    @PostMapping("/{tagId}")
    fun addTagToPicture(
        @PathVariable pictureId: String,
        @PathVariable tagId: String
    ): Mono<Tag>  = pictureTagsService.addTagToPicture(pictureId, tagId)

    @DeleteMapping("/{tagId}")
    fun removeTagFromPicture(
        @PathVariable pictureId: String,
        @PathVariable tagId: String
    ): Mono<Void> = pictureTagsService.removeTagFromPicture(pictureId, tagId)
}
