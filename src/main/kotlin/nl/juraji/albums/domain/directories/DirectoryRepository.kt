package nl.juraji.albums.domain.directories

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DirectoryRepository : ReactiveNeo4jRepository<Directory, String> {

    fun findByLocation(location: String): Mono<Directory>

    fun findByLocationStartingWith(location: String): Flux<Directory>

    @Query(
        """
            MATCH (d:Directory) WHERE d.id = $ directoryId
            MATCH (p:Picture) WHERE p.id = $ pictureId
            MERGE (d)-[:CONTAINS]->(p)
        """
    )
    fun addPicture(directoryId: String, pictureId: String): Mono<Void>

    @Query(
        """
            MATCH (p:Directory) WHERE p.id = $ parentId
            MATCH (d:Directory) WHERE d.id = $ directoryId
            MERGE (p)-[:PARENT_OF]->(d)
        """
    )
    fun addChild(parentId: String, directoryId: String): Mono<Void>
}
