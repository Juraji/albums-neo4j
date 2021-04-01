package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.PicturesService
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/pictures")
class PicturesController(
    private val picturesService: PicturesService,
    private val duplicatesService: DuplicatesService
) {

    @GetMapping("/{pictureId}/download")
    fun downloadPicture(
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
        @PathVariable pictureId: String,
        @PathVariable targetId: String,
    ): Mono<Void> = duplicatesService.removeDuplicate(pictureId, targetId)

    companion object {
        const val IMAGE_CACHE_HOURS: Long = 1
    }
}
