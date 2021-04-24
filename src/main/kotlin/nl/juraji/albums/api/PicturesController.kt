package nl.juraji.albums.api

import nl.juraji.albums.api.dto.PictureContainerDto
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.FoldersService
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.util.concurrent.TimeUnit
import javax.validation.Valid

@RestController
@RequestMapping("/pictures")
class PicturesController(
    private val picturesService: PicturesService,
    private val foldersService: FoldersService,
    private val duplicatesService: DuplicatesService
) {

    @GetMapping("/{pictureId}")
    fun getPicture(
        @PathVariable pictureId: String
    ): Mono<PictureContainerDto> =
        Mono.zip(picturesService.getById(pictureId), foldersService.getByPictureId(pictureId))
            .map { (picture, folder) -> PictureContainerDto(picture, folder) }

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

    @PostMapping("/{pictureId}/move-to/{targetId}")
    fun movePicture(
        @PathVariable pictureId: String,
        @PathVariable targetId: String
    ): Mono<Picture> = picturesService.movePicture(pictureId, targetId)

    @PutMapping("/{pictureId}")
    fun updatePicture(
        @PathVariable pictureId: String,
        @Valid @RequestBody picture: Picture
    ): Mono<Picture> = picturesService.updatePicture(pictureId, picture)

    @DeleteMapping("/{pictureId}/duplicates/{targetId}")
    fun deleteDuplicateFromPicture(
        @PathVariable pictureId: String,
        @PathVariable targetId: String,
    ): Mono<Void> = duplicatesService.removeDuplicate(pictureId, targetId)

    @DeleteMapping("/{pictureId}")
    fun deletePicture(
        @PathVariable pictureId: String
    ): Mono<Void> = picturesService.deletePicture(pictureId)

    companion object {
        const val IMAGE_CACHE_HOURS: Long = 1
    }
}
