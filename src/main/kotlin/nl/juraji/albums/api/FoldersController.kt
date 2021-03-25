package nl.juraji.albums.api

import nl.juraji.albums.domain.FoldersService
import nl.juraji.albums.domain.folders.Folder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/folders")
class FoldersController(
    private val foldersService: FoldersService
) {

    @GetMapping("/roots")
    fun getRoots(): Flux<Folder> = foldersService.getRoots()

    @GetMapping("/{folderId}/children")
    fun getFolderChildren(
        @PathVariable folderId: String
    ): Flux<Folder> = foldersService.getFolderChildren(folderId)

    @PostMapping
    fun createFolder(
        @RequestParam(name = "parentId", required = false) parentFolderId: String?,
        @RequestBody folder: Folder,
    ): Mono<Folder> = foldersService.createFolder(folder, parentFolderId)

    @PutMapping("/{folderId}")
    fun updateFolder(
        @PathVariable folderId: String,
        @RequestBody folder: Folder,
    ): Mono<Folder> = foldersService.updateFolder(folderId, folder)

    @DeleteMapping("/{folderId}")
    fun deleteFolder(
        @PathVariable folderId: String,
        @RequestParam("recursive", required = false, defaultValue = "false") recursive: Boolean
    ): Mono<Void> = foldersService.deleteFolder(folderId, recursive)

    @PostMapping("/{folderId}/move-to/{targetId}")
    fun moveFolder(
        @PathVariable folderId: String,
        @PathVariable targetId: String,
    ):Mono<Folder> = foldersService.moveFolder(folderId, targetId)
}
