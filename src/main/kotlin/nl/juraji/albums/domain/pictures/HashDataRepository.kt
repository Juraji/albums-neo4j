package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface HashDataRepository : ReactiveNeo4jRepository<HashData, String> {

    @Query("MATCH (:Picture {id: $ pictureId})-[:DESCRIBED_BY]->(d:HashData) RETURN d")
    fun findByPictureId(pictureId: String): Mono<HashData>

    @Query(
        """
            MATCH (p:Picture) WHERE p.id = $ pictureId
            MATCH (d:HashData) WHERE d.id = $ hashDataId
            CREATE (p)-[:DESCRIBED_BY]->(d)
        """
    )
    fun setPictureHashData(pictureId: String, hashDataId: String): Mono<Void>
}
