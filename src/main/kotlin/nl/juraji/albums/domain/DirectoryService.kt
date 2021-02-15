package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.*
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.extra.bool.not
import java.nio.file.Path

@Service
class DirectoryService(
    private val fileOperations: FileOperations,
    private val directoryRepository: DirectoryRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getDirectory(directoryId: String): Mono<DirectoryProps> = directoryRepository.findDirectoryPropsById(directoryId)

    fun getRootDirectories(): Flux<Directory> = directoryRepository.findRoots()

    fun createDirectory(location: String, recursive: Boolean = false): Mono<Directory> = Mono.just(location.toPath())
        .flatMap {
            if (recursive) this.addDirectoryRecursive(it)
            else this.addDirectory(it)
        }

    fun deleteDirectory(directoryId: String): Mono<Unit> = directoryRepository
        .deleteTreeById(directoryId)
        .mapToUnit()

    fun findAndLinkParent(directoryId: String): Mono<Unit> = directoryRepository
        .findById(directoryId)
        .map { it.location.toPath() }
        .filter { it.parent != null }
        .flatMap { directoryRepository.findByLocation(it.parent.toString()) }
        .flatMap { directoryRepository.addChild(it.id!!, directoryId).thenReturn(it) }
        .doOnNext { applicationEventPublisher.publishEvent(DirectoryTreeUpdatedEvent(it.id!!)) }
        .mapToUnit()

    fun findAndLinkChildren(directoryId: String): Flux<Unit> = directoryRepository
        .findById(directoryId)
        .flatMapMany { d ->
            val childPathCount = d.location.toPath().count() + 1
            directoryRepository
                .findByLocationStartingWith(d.location)
                .filter { it.location.toPath().count() == childPathCount } // Only to direct children
        }
        .flatMap { directoryRepository.addChild(directoryId, it.id!!).thenReturn(it) }
        .doOnComplete { applicationEventPublisher.publishEvent(DirectoryTreeUpdatedEvent(directoryId)) }
        .mapToUnit()

    private fun addDirectory(location: Path) = Mono.just(location)
        .validateAsync {
            isFalse(directoryRepository.existsByLocation(it.toString())) { "Directory with path $location already exists" }
        }
        .flatMap { directoryRepository.save(Directory(location = it.toString(), name = it.fileName.toString())) }
        .doOnNext { applicationEventPublisher.publishEvent(DirectoryCreatedEvent(it.id!!)) }

    private fun addDirectoryRecursive(location: Path) = fileOperations
        .listDirectories(location, true)
        .map { Directory(location = it.toString(), name = it.fileName.toString()) }
        .filterWhen { directoryRepository.existsByLocation(it.location).not() }
        .flatMap { directoryRepository.save(it) }
        .doOnNext { applicationEventPublisher.publishEvent(DirectoryCreatedEvent(it.id!!)) }
        .collectList()
        .filter(MutableList<Directory>::isNotEmpty)
        .map { it.first() }
}
