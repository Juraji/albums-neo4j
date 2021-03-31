package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/folders/{folderId}/pictures")
class PicturesController(
    private val picturesService: PicturesService,
    private val duplicatesService: DuplicatesService
) {

    @GetMapping
    fun getFolderPictures(
        @PathVariable folderId: String
    ): Flux<Picture> = picturesService.getFolderPictures(folderId)

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadPicture(
        @PathVariable folderId: String,
        @RequestPart("files[]") files: Flux<FilePart>
    ): Flux<Picture> = files
        .flatMap { picturesService.persistNewPicture(folderId, it) }

    @GetMapping("/{pictureId}/download")
    fun downloadPicture(
        @PathVariable folderId: String,
        @PathVariable pictureId: String,
    ): Mono<ResponseEntity<Resource>> = picturesService
        .getPictureResource(pictureId)
        .map {
            ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(IMAGE_CACHE_HOURS, TimeUnit.HOURS))
                .body(it)
        }

    @GetMapping("/{pictureId}/thumbnail")
    fun downloadThumbnail(
        @PathVariable folderId: String,
        @PathVariable pictureId: String,
    ): Mono<ResponseEntity<Resource>> = picturesService
        .getThumbnailResource(pictureId)
        .map {
            ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(IMAGE_CACHE_HOURS, TimeUnit.HOURS))
                .body(it)
        }

    @DeleteMapping("/{pictureId}/duplicates/{targetId}")
    fun deleteDuplicateFromPicture(
        @PathVariable folderId: String,
        @PathVariable pictureId: String,
        @PathVariable targetId: String,
    ): Mono<Void> = duplicatesService.removeDuplicate(pictureId, targetId)

    companion object {
        const val IMAGE_CACHE_HOURS: Long = 1
    }
}
