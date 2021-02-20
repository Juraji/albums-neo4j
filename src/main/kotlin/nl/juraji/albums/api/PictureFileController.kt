package nl.juraji.albums.api

import nl.juraji.albums.domain.PictureService
import org.springframework.core.io.PathResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pictures/{pictureId}/files")
class PictureFileController(
    private val pictureService: PictureService
) {

    @GetMapping("/image")
    fun getPictureImage(
        @PathVariable pictureId: String
    ): Mono<Resource> = pictureService
        .getImageLocationById(pictureId)
        .map(::PathResource)
}
