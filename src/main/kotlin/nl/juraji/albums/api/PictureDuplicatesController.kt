package nl.juraji.albums.api

import nl.juraji.albums.api.dto.DuplicatedByProps
import nl.juraji.albums.api.dto.toDuplicatedByProps
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.duplicates.DuplicatedBy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/pictures/{pictureId}/duplicates")
class PictureDuplicatesController(
    private val duplicatesService: DuplicatesService
) {
    @GetMapping
    fun getDuplicatedByPicture(
        @PathVariable pictureId: String
    ): Flux<DuplicatedByProps> = duplicatesService
        .findByPictureId(pictureId)
        .map(DuplicatedBy::toDuplicatedByProps)
}
