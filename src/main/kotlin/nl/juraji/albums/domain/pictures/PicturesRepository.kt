package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PicturesRepository : ReactiveNeo4jRepository<Picture, String> {

    @Query(
        """
        MATCH (p:Picture)<-[:HAS_PICTURE]-(:Folder {id: $ folderId})
        RETURN p ORDER BY p.name
    """
    )
    fun findAllByFolderId(folderId: String): Flux<Picture>

    @Query(
        """
        MATCH (f:Folder {id: $ folderId})
        MATCH (p:Picture {id: $ pictureId})
        
        MERGE (f)-[:HAS_PICTURE]->(p)
        RETURN p
    """
    )
    fun addPictureToFolder(folderId: String, pictureId: String): Mono<Picture>
}
