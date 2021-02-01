package nl.juraji.albums.api

import nl.juraji.albums.model.Directory
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.services.FileSystemService
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DirectoryService(
    private val directoryRepository: DirectoryRepository,
    private val fileSystemService: FileSystemService
) {
    fun getDirectories(): Flux<Directory> = directoryRepository.findAll()

    fun getDirectory(directoryId: Long): Mono<Directory> = directoryRepository.findById(directoryId)

    fun createDirectory(directory: Directory): Mono<Directory> = Mono
        .just(directory)
        .validateAsync {
            isTrue(fileSystemService.exists(it.location.toPath())) { "Directory ${directory.location} does not exist on disk" }
        }
        .flatMap { directoryRepository.save(directory) }


}
