package nl.juraji.albums.domain

import nl.juraji.albums.domain.folders.Folder
import nl.juraji.albums.domain.folders.FolderTreeView
import nl.juraji.albums.domain.folders.FoldersRepository
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FoldersService(
    private val foldersRepository: FoldersRepository
) {
    fun getTree(): Flux<FolderTreeView> = foldersRepository
        .findRoots()
        .flatMap(this::createFolderTreeView)

    private fun createFolderTreeView(folder: Folder): Mono<FolderTreeView> = foldersRepository
        .findChildren(folder.id!!)
        .flatMap(this::createFolderTreeView)
        .collectList()
        .map {
            FolderTreeView(
                id = folder.id,
                name = folder.name,
                children = it
            )
        }

    fun createFolder(folder: Folder, parentFolderId: String): Mono<Folder> = Mono.just(folder)
        .validateAsync {
            unless(parentFolderId.isBlank()) {
                isTrue(foldersRepository.existsById(parentFolderId)) { "Folder with id $parentFolderId does not exist, can not link as parent" }
            }
        }
        .flatMap { foldersRepository.save(folder.copy(id = null)) }
        .doOnNext { if (parentFolderId.isNotBlank()) foldersRepository.setParent(it.id!!, parentFolderId) }

    fun updateFolder(folderId: String, update: Folder): Mono<Folder> = foldersRepository
        .findById(folderId)
        .map { it.copy(name = update.name) }
        .flatMap(foldersRepository::save)

    fun deleteFolder(folderId: String, recursive: Boolean): Mono<Void> = Mono.just(folderId)
        .validateAsync {
            unless(recursive) {
                isTrue(foldersRepository.isEmptyById(it)) { "Folder is not empty, set recursive=true to ignore this message" }
            }
        }
        .flatMap(foldersRepository::deleteById)

    fun moveFolder(folderId: String, targetId: String): Mono<Folder> =
        validateAsync {
            isTrue(foldersRepository.existsById(folderId)) { "Folder with id $folderId does not exist" }
            isTrue(foldersRepository.existsById(targetId)) { "Folder with id $folderId does not exist" }
        }
            .flatMap { foldersRepository.setParent(folderId, targetId) }
}
