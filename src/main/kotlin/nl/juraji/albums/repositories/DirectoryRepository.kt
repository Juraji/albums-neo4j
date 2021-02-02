package nl.juraji.albums.repositories

import nl.juraji.albums.model.Directory
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DirectoryRepository : ReactiveNeo4jRepository<Directory, String> {
    fun findByLocation(location: String): Mono<Directory>
}
