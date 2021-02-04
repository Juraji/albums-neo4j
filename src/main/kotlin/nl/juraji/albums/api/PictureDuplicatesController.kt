package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pictures/{pictureId}/duplicates")
class PictureDuplicatesController(
    private val duplicatesService: DuplicatesService
) {

    @DeleteMapping("/{targetId}")
    fun removeDuplicateFromPicture(
        @PathVariable pictureId: String,
        @PathVariable targetId: String
    ): Mono<Unit> = duplicatesService.removeDuplicateFromPicture(pictureId, targetId)
}
