package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PictureHashesRepository : ReactiveNeo4jRepository<PictureHash, String> {

    @Query(
        """
        MATCH (ph:PictureHash)<-[:DESCRIBES]-(:Picture {id: $ pictureId})
        RETURN ph
    """
    )
    fun findByPictureId(pictureId: String): Mono<PictureHash>

    @Query(
        """
        MATCH(p:Picture {id: $ pictureId})
        MATCH(ph:PictureHash {id: $ hashDataId})
        
        MERGE (ph)-[:DESCRIBES]->(p)
        RETURN ph
    """
    )
    fun linkToPicture(hashDataId: String, pictureId: String): Mono<PictureHash>
}
