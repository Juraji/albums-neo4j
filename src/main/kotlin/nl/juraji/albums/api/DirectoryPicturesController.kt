package nl.juraji.albums.api

import nl.juraji.albums.api.dto.PictureProps
import nl.juraji.albums.api.dto.toPictureProps
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.Picture
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/directories/{directoryId}")
class DirectoryPicturesController(
    private val directoryService: DirectoryService,
    private val pictureService: PictureService
) {

    @GetMapping("/pictures")
    fun getPicturesByDirectoryId(
        @PathVariable directoryId: String,
        @RequestParam("page") pageIndex: Int,
        @RequestParam("size") pageSize: Int
    ): Flux<PictureProps> = pictureService
        .getByDirectoryId(directoryId, PageRequest.of(pageIndex, pageSize))
        .map(Picture::toPictureProps)

    @PostMapping("/update")
    fun updateDirectoryPictures(
        @PathVariable directoryId: String
    ): Mono<Unit> = directoryService.updatePicturesFromDisk(directoryId)
}
