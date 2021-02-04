package nl.juraji.albums.api

import nl.juraji.albums.api.dto.DirectoryDto
import nl.juraji.albums.api.dto.toDirectoryDto
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.DirectoryDescription
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
    fun getAllDirectories(): Flux<DirectoryDto> = directoryService
        .getAllDirectories()
        .map(DirectoryDescription::toDirectoryDto)

    @GetMapping("/{directoryId}")
    fun getDirectory(
        @PathVariable("directoryId") directoryId: String
    ): Mono<DirectoryDto> = directoryService
        .getDirectory(directoryId)
        .map(DirectoryDescription::toDirectoryDto)
}
