package nl.juraji.albums.api

import nl.juraji.albums.model.Directory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/directories")
class DirectoryController(
    private val directoryService: DirectoryService
) {

    @GetMapping
    fun getAllDirectories(): Flux<Directory> = directoryService.getAllDirectories()

    @GetMapping("/{directoryId}")
    fun getDirectory(
        @PathVariable("directoryId") directoryId: String
    ): Mono<Directory> = directoryService.getDirectory(directoryId)
}
