package nl.juraji.albums.api

import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/folders/{folderId}/pictures")
class FolderPicturesController(
    private val picturesService: PicturesService
) {
    @GetMapping
    fun getFolderPictures(
        @PathVariable folderId: String
    ): Flux<Picture> = picturesService.getFolderPictures(folderId)

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadPictures(
        @PathVariable folderId: String,
        @RequestPart("files[]") files: Flux<FilePart>,
    ): Flux<Picture> = files
        .flatMap { picturesService.persistNewPicture(folderId, it) }
}
