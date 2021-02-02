package nl.juraji.albums.api

import nl.juraji.albums.model.Directory
import nl.juraji.albums.repositories.DirectoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DirectoryService(
    private val directoryRepository: DirectoryRepository,
) {
    fun getAllDirectories(): Flux<Directory> = directoryRepository.findAll()

    fun getDirectory(directoryId: String): Mono<Directory> = directoryRepository.findById(directoryId)
}
