package nl.juraji.albums.api

import nl.juraji.albums.api.dto.NewPictureDto
import nl.juraji.albums.api.dto.PictureDto
import nl.juraji.albums.api.dto.TagDto
import nl.juraji.albums.api.dto.toPictureDto
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.PictureDescription
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
    fun getAllPictures(): Flux<PictureDescription> = pictureService.getAllPictures()

    @GetMapping("/{pictureId}")
    fun getPicture(
        @PathVariable("pictureId") pictureId: String
    ): Mono<PictureDescription> = pictureService.getPicture(pictureId)

    @PostMapping
    fun addPicture(
        @Valid @RequestBody picture: NewPictureDto
    ): Mono<PictureDto> = pictureService
        .addPicture(picture.location, picture.name)
        .map(Picture::toPictureDto)

    @PostMapping("/{pictureId}/tags")
    fun tagPictureBy(
        @PathVariable("pictureId") pictureId: String,
        @Valid @RequestBody tag: TagDto
    ): Mono<PictureDto> = pictureService
        .tagPictureBy(pictureId, tag.id)
        .map(Picture::toPictureDto)
}
