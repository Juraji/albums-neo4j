package nl.juraji.albums.api

import nl.juraji.albums.api.dto.NewPictureDto
import nl.juraji.albums.api.dto.PictureProps
import nl.juraji.albums.api.dto.toPictureProps
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/directories/{directoryId}")
class DirectoryPicturesController(
    private val pictureService: PictureService
) {

    @GetMapping("/pictures")
    fun getPicturesByDirectoryId(
        @PathVariable directoryId: String
    ): Flux<PictureProps> = pictureService
        .getByDirectoryId(directoryId)
        .map(Picture::toPictureProps)

    @PostMapping("/pictures")
    fun addPicture(
        @PathVariable directoryId: String,
        @Valid @RequestBody picture: NewPictureDto
    ): Mono<PictureProps> = pictureService
        .addPicture(directoryId, picture.location, picture.name)
        .map(Picture::toPictureProps)
}
