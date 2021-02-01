package nl.juraji.albums.api

import nl.juraji.albums.model.Directory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class DirectoryController(
    private val directoryService: DirectoryService
) {

    @GetMapping("/directories")
    fun getDirectories(): Flux<Directory> = directoryService.getDirectories()

    @GetMapping("/directories/{directoryId}")
    fun getDirectory(
        @PathVariable("directoryId") directoryId: Long
    ): Mono<Directory> = directoryService.getDirectory(directoryId)

    @PostMapping("/directories")
    fun createDirectory(
        @RequestBody directory: Directory
    ): Mono<Directory> = directoryService.createDirectory(directory)
}
