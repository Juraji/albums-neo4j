package nl.juraji.albums.api

import nl.juraji.albums.domain.PictureService
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/pictures/{pictureId}/files")
class PictureFileController(
    private val pictureService: PictureService
) {

    @GetMapping("/image")
    fun getPictureImage(
        @PathVariable pictureId: String
    ): Mono<ResponseEntity<Resource>> = pictureService
        .getImageLocationById(pictureId)
        .map(::FileSystemResource)
        .map {
            ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(IMAGE_CACHE_HOURS, TimeUnit.HOURS))
                .body(it)
        }

    companion object {
        const val IMAGE_CACHE_HOURS: Long = 1
    }
}
