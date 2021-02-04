package nl.juraji.albums.api

import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.Directory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
