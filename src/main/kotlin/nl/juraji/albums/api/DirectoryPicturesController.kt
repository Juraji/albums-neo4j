package nl.juraji.albums.api

import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.PictureProps
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/directories/{directoryId}")
class DirectoryPicturesController(
    private val pictureService: PictureService
) {

    @GetMapping("/pictures")
    fun getPicturesByDirectoryId(
        @PathVariable directoryId: String,
        @RequestParam("page") pageIndex: Int,
        @RequestParam("size") pageSize: Int
    ): Flux<PictureProps> = pictureService
        .getByDirectoryId(directoryId, PageRequest.of(pageIndex, pageSize))
}
