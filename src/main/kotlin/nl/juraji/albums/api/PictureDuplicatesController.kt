package nl.juraji.albums.api

import nl.juraji.albums.api.dto.DuplicateProps
import nl.juraji.albums.api.dto.toDuplicateProps
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.duplicates.Duplicate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/pictures/{pictureId}/duplicates")
class PictureDuplicatesController(
    private val duplicatesService: DuplicatesService
) {
    @PostMapping("scan")
    fun scanPictureDuplicates(
        @PathVariable pictureId: String
    ): Flux<DuplicateProps> = duplicatesService
        .scanDuplicatesForPicture(pictureId)
        .map(Duplicate::toDuplicateProps)
}
