package nl.juraji.albums.domain

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.util.toPath
import nl.juraji.albums.util.toUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DirectoryService(
    private val directoryRepository: DirectoryRepository,
) {
    fun getAllDirectories(): Flux<Directory> = directoryRepository.findAll()

    fun getDirectory(directoryId: String): Mono<Directory> = directoryRepository.findById(directoryId)

    fun addPicture(picture: Picture): Mono<Directory> {
        val location = picture.location.toPath().parent.toString()
        return directoryRepository.findByLocation(location)
            .flatMap { d ->
                directoryRepository
                    .addPicture(d.id!!, picture.id!!)
                    .thenReturn(d)
            }
    }

    fun createDirectory(location: String): Mono<Directory> = directoryRepository
        .save(Directory(location = location))

    fun deleteDirectory(directoryId: String): Mono<Unit> = directoryRepository
        .deleteById(directoryId)
        .toUnit()
}
