package nl.juraji.albums.api

import nl.juraji.albums.model.DirectoryDescription
import nl.juraji.albums.repositories.DirectoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DirectoryService(
    private val directoryRepository: DirectoryRepository,
) {
    fun getAllDirectories(): Flux<DirectoryDescription> = directoryRepository.findAllDescriptions()

    fun getDirectory(directoryId: String): Mono<DirectoryDescription> = directoryRepository.findDescriptionById(directoryId)
}
