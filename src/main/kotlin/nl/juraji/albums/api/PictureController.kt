package nl.juraji.albums.api

import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/pictures")
class PictureController(
    private val pictureService: PictureService
) {

    @GetMapping
    fun getAllPictures(): Flux<Picture> = pictureService.getAllPictures()

    @GetMapping("/{pictureId}")
    fun getPicture(
        @PathVariable("pictureId") pictureId: String
    ): Mono<Picture> = pictureService.getPicture(pictureId)

    @PostMapping
    fun addPicture(
        @Valid @RequestBody picture: Picture
    ): Mono<Picture> = pictureService.addPicture(picture)

    @PostMapping("/{pictureId}/tags")
    fun tagPictureBy(
        @PathVariable("pictureId") pictureId: String,
        @Valid @RequestBody tag: Tag
    ): Mono<Picture> = pictureService.tagPictureBy(pictureId, tag)
}
