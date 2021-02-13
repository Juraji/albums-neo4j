package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.extra.bool.not

@Service
class DirectoryService(
    private val fileOperations: FileOperations,
    private val directoryRepository: DirectoryRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getAllDirectories(): Flux<Directory> = directoryRepository.findAll()

    fun getDirectory(directoryId: String): Mono<Directory> = directoryRepository.findById(directoryId)

    fun createDirectory(location: String, recursive: Boolean = false): Mono<Directory> =
        if (recursive) {
            fileOperations.listDirectories(location.toPath(), true)
                .map { Directory(location = it.toString(), name = it.fileName.toString()) }
                .filterWhen { directoryRepository.existsByLocation(it.location).not() }
                .flatMap { directoryRepository.save(it) }
                .doOnNext { applicationEventPublisher.publishEvent(DirectoryCreatedEvent(this, it.id!!)) }
                .collectList()
                .filter(MutableList<Directory>::isNotEmpty)
                .map { it.first() }
        } else {
            directoryRepository
                .save(Directory(location = location, name = location.toPath().fileName.toString()))
                .doOnNext { applicationEventPublisher.publishEvent(DirectoryCreatedEvent(this, it.id!!)) }
        }

    fun deleteDirectory(directoryId: String): Mono<Unit> = directoryRepository
        .deleteById(directoryId)
        .mapToUnit()

    fun findAndLinkParent(directoryId: String): Mono<Unit> = directoryRepository
        .findById(directoryId)
        .map { it.location.toPath() }
        .filter { it.parent != null }
        .flatMap { directoryRepository.findByLocation(it.parent.toString()) }
        .flatMap { directoryRepository.addChild(it.id!!, directoryId) }
        .mapToUnit()

    fun findAndLinkChildren(directoryId: String): Flux<Unit> = directoryRepository
        .findById(directoryId)
        .flatMapMany { d ->
            val childPathCount = d.location.toPath().count() + 1
            directoryRepository
                .findByLocationStartingWith(d.location)
                .filter { it.location.toPath().count() == childPathCount } // Only to direct children
        }
        .flatMap { directoryRepository.addChild(directoryId, it.id!!) }
        .mapToUnit()
}
