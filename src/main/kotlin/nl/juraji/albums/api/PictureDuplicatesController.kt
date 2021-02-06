package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.relationships.DuplicatedBy
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pictures/{pictureId}/duplicates")
class PictureDuplicatesController(
    private val duplicatesService: DuplicatesService
) {

    @GetMapping
    fun getDuplicatedByPicture(
        @PathVariable pictureId: String
    ): Flux<DuplicatedBy> = duplicatesService.findDuplicatedByByPictureId(pictureId)

    @DeleteMapping("/{targetId}")
    fun removeDuplicateFromPicture(
        @PathVariable pictureId: String,
        @PathVariable targetId: String
    ): Mono<Unit> = duplicatesService.unsetDuplicatePicture(pictureId, targetId)
}
