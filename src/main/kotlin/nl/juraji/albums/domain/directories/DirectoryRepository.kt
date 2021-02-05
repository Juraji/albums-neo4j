package nl.juraji.albums.domain.directories

import nl.juraji.albums.util.CypherQuery
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DirectoryRepository : ReactiveNeo4jRepository<Directory, String> {

    fun findByLocation(location: String): Mono<Directory>

    // language=cypher
    @CypherQuery(
        """
            MATCH (d:Directory) WHERE d.id = $ directoryId
            MATCH (p:Picture) WHERE p.id = $ pictureId
            CREATE (d)-[:CONTAINS]->(p)
        """
    )
    fun addPicture(directoryId: String, pictureId: String): Mono<Void>

    @CypherQuery(
        """
            MERGE (d:Directory {location: $ location})
            ON CREATE SET d.id = randomUUID()
            RETURN d
        """
    )
    fun mergeByLocation(location: String): Mono<Directory>
}
