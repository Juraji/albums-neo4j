package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.tags.Tag
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PictureTagsRepository : ReactiveNeo4jRepository<Tag, String> {

    @Query(
        """
        MATCH (:Picture {id: $ pictureId})-[:HAS_TAG]->(t:Tag)
        RETURN t ORDER BY t.label
    """
    )
    fun findPictureTags(pictureId: String): Flux<Tag>

    @Query(
        """
        MATCH (p:Picture {id: $ pictureId})
        MATCH (t:Tag {id: $ tagId})
        
        MERGE (p)-[:HAS_TAG]->(t)
        RETURN t
    """
    )
    fun addTagToPicture(pictureId: String, tagId: String): Mono<Tag>

    @Query(
        """
            MATCH (:Picture {id: $ pictureId})-[rel:HAS_TAG]->(:Tag {id: $ tagId})
            DELETE rel
        """
    )
    fun removeTagFromPicture(pictureId: String, tagId: String): Mono<Void>
}
