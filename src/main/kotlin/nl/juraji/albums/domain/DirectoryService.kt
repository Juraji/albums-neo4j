package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.toPath
import nl.juraji.albums.util.toUnit
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DirectoryService(
    private val directoryRepository: DirectoryRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getAllDirectories(): Flux<Directory> = directoryRepository.findAll()

    fun getDirectory(directoryId: String): Mono<Directory> = directoryRepository.findById(directoryId)

    fun createDirectory(location: String): Mono<Directory> = directoryRepository
        .save(Directory(location = location, name = location.toPath().fileName.toString()))
        .doOnNext { applicationEventPublisher.publishEvent(DirectoryCreatedEvent(this, it)) }

    fun deleteDirectory(directoryId: String): Mono<Unit> = directoryRepository
        .deleteById(directoryId)
        .toUnit()
}
