package nl.juraji.albums.api

import nl.juraji.albums.api.dto.NewDirectoryDto
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryProps
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/directories")
class DirectoriesController(
    private val directoryService: DirectoryService
) {

    @GetMapping("/roots")
    fun getRootDirectories(): Flux<Directory> = directoryService.getRootDirectories()

    @GetMapping("/{directoryId}")
    fun getDirectory(
        @PathVariable("directoryId") directoryId: String
    ): Mono<DirectoryProps> = directoryService.getDirectory(directoryId)

    @PostMapping
    fun createDirectory(
        @RequestParam("recursive", required = false, defaultValue = "false") recursive: Boolean,
        @Valid @RequestBody newDirectoryDto: NewDirectoryDto
    ): Mono<Directory> = directoryService.createDirectory(newDirectoryDto.location, recursive)

    @DeleteMapping("/{directoryId}")
    fun deleteDirectory(
        @PathVariable directoryId: String
    ): Mono<Unit> = directoryService.deleteDirectory(directoryId)
}
