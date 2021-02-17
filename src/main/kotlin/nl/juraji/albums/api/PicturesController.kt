package nl.juraji.albums.api

import nl.juraji.albums.api.dto.NewPictureDto
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/pictures")
class PicturesController(
    private val pictureService: PictureService
) {

    @GetMapping("/{pictureId}")
    fun getPicture(
        @PathVariable("pictureId") pictureId: String
    ): Mono<Picture> = pictureService.getPicture(pictureId)

    @PostMapping
    fun addPicture(
        @Valid @RequestBody picture: NewPictureDto
    ): Mono<Picture> = pictureService.addPicture(picture.location, picture.name)

    @DeleteMapping("/{pictureId}")
    fun deletePicture(
        @PathVariable pictureId: String,
        @RequestParam("deleteFile", required = false) deleteFile: Boolean?
    ): Mono<Unit> = pictureService.deletePicture(pictureId, deleteFile ?: false)

    @PostMapping("/{pictureId}/tags/{tagId}")
    fun tagPictureBy(
        @PathVariable("pictureId") pictureId: String,
        @PathVariable tagId: String,
    ): Mono<Unit> = pictureService.addTag(pictureId, tagId)

    @DeleteMapping("/{pictureId}/tags/{tagId}")
    fun removeTagFromPicture(
        @PathVariable pictureId: String,
        @PathVariable tagId: String
    ): Mono<Unit> = pictureService.removeTag(pictureId, tagId)
}
