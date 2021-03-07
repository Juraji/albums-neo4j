package nl.juraji.albums.api

import nl.juraji.albums.api.dto.DuplicatedByProps
import nl.juraji.albums.api.dto.toDuplicatedByProps
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.duplicates.Duplicate
import nl.juraji.albums.domain.duplicates.DuplicatedBy
import org.springframework.web.bind.annotation.*
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

    @PostMapping("scan")
    fun scanPictureDuplicates(
        @PathVariable pictureId: String
    ): Flux<Duplicate> = duplicatesService
        .scanDuplicatesForPicture(pictureId)
}
