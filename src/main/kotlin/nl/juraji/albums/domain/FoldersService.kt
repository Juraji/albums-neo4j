package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.FolderCreatedEvent
import nl.juraji.albums.domain.events.FolderDeletedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.folders.Folder
import nl.juraji.albums.domain.folders.FolderTreeView
import nl.juraji.albums.domain.folders.FoldersRepository
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.file.Path

@Service
class FoldersService(
    private val foldersRepository: FoldersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ReactiveEventListener() {
    fun getTree(): Flux<FolderTreeView> = foldersRepository
        .findRoots()
        .flatMap { f -> this.createFolderTreeView(f, true) }

    fun getByPictureId(pictureId: String): Mono<Folder> = foldersRepository.findByPictureId(pictureId)

    private fun createFolderTreeView(folder: Folder, isRoot: Boolean = false): Mono<FolderTreeView> = foldersRepository
        .findChildren(folder.id!!)
        .flatMap(this::createFolderTreeView)
        .collectList()
        .map {
            FolderTreeView(
                id = folder.id,
                name = folder.name,
                children = it,
                isRoot = isRoot
            )
        }

    @Transactional
    fun createFolder(folder: Folder, parentFolderId: String): Mono<Folder> = Mono.just(folder)
        .validateAsync {
            unless(parentFolderId == ROOT_FOLDER_ID) {
                isTrue(foldersRepository.existsById(parentFolderId)) { "Folder with id $parentFolderId does not exist, can not link as parent" }
            }
        }
        .flatMap { foldersRepository.save(folder.copy(id = null)) }
        .flatMap {
            if (parentFolderId == ROOT_FOLDER_ID) Mono.just(it)
            else foldersRepository.setParent(it.id!!, parentFolderId).thenReturn(it)
        }
        .doOnNext { applicationEventPublisher.publishEvent(FolderCreatedEvent(it.id!!, it.name, parentFolderId)) }

    @Transactional
    fun createFolderP(path: String, parentFolderId: String): Mono<Folder> = Mono
        .just(path)
        .map(String::toPath)
        .flatMap { createFolderPInternal(it, parentFolderId) }

    private fun createFolderPInternal(path: Path, parentFolderId: String): Mono<Folder> {
        return if (path.nameCount == 0) {
            foldersRepository.findById(parentFolderId)
        } else {
            val rName = path.first().toString()
            val hasMore = path.nameCount > 1

            val existing =
                if (parentFolderId == ROOT_FOLDER_ID) foldersRepository.findRootByName(rName)
                else foldersRepository.findByNameAndParentId(rName, parentFolderId)

            existing
                .switchIfEmpty { createFolder(Folder(name = rName), parentFolderId) }
                .flatMap { nf ->
                    if (hasMore) createFolderPInternal(
                        path.subpath(1, path.nameCount),
                        nf.id!!
                    ) else Mono.just(nf)
                }
        }
    }

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
        .flatMap(foldersRepository::deleteRecursivelyById)
        .doFinally { applicationEventPublisher.publishEvent(FolderDeletedEvent(folderId)) }

    fun moveFolder(folderId: String, targetId: String): Mono<Folder> =
        validateAsync {
            isTrue(foldersRepository.existsById(folderId)) { "Folder with id $folderId does not exist" }

            unless(targetId == ROOT_FOLDER_ID) {
                isTrue(foldersRepository.existsById(targetId)) { "Folder with id $folderId does not exist" }
            }
        }
            .flatMap {
                if (targetId == ROOT_FOLDER_ID) foldersRepository.unsetParent(folderId)
                else foldersRepository.setParent(folderId, targetId)
            }

//    @Async
//    @EventListener(FolderCreatedEvent::class, condition = "#e.parentId != 'ROOT'")
//    fun onFolderCreatedWithParent(e: FolderCreatedEvent) = consumePublisher {
//        foldersRepository.setParent(e.folderId, e.parentId)
//    }

    companion object {
        const val ROOT_FOLDER_ID = "ROOT"
    }
}
