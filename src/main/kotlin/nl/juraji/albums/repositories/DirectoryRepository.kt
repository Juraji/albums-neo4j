package nl.juraji.albums.repositories

import nl.juraji.albums.model.Directory
import nl.juraji.albums.model.DirectoryDescription
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DirectoryRepository : ReactiveNeo4jRepository<Directory, String> {
    fun findByLocation(location: String): Mono<Directory>

    fun findDescriptionById(id: String): Mono<DirectoryDescription>

    // language=cypher
    @Query("MATCH (d:Directory) RETURN d")
    fun findAllDescriptions(): Flux<DirectoryDescription>
}
