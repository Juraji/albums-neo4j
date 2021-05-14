package nl.juraji.albums.api

import kotlinx.coroutines.sync.Semaphore
import nl.juraji.albums.api.exceptions.RateLimitExceededException
import nl.juraji.albums.configuration.ImageUploadRateLimitsConfiguration
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicBoolean

@RestController
@RequestMapping("/folders/{folderId}/pictures")
class FolderPicturesController(
    private val picturesService: PicturesService,
    rateLimitsConfiguration: ImageUploadRateLimitsConfiguration
) {
    private val uploadTaskSem = Semaphore(rateLimitsConfiguration.maxConcurrent)

    @GetMapping
    fun getFolderPictures(
        @PathVariable folderId: String
    ): Flux<Picture> = picturesService.getFolderPictures(folderId)

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadPictures(
        @PathVariable folderId: String,
        @RequestPart("files[]") files: Flux<FilePart>,
    ): Flux<Picture> {
        return if (uploadTaskSem.tryAcquire()) files
            .flatMap { picturesService.persistNewPicture(folderId, it) }
            .doFinally { uploadTaskSem.release() }
        else throw RateLimitExceededException()
    }
}
