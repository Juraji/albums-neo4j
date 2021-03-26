package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface HashDataRepository : ReactiveNeo4jRepository<HashData, String> {

    @Query(
        """
        MATCH(p:Picture {id: $ pictureId})
        MATCH(hd:HashData {id: $ hashDataId})
        
        MERGE (hd)-[:DESCRIBES]->(p)
        RETURN hd
    """
    )
    fun linkToPicture(hashDataId: String, pictureId: String): Mono<HashData>
}
