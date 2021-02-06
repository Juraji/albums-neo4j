package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PictureRepository : ReactiveNeo4jRepository<Picture, String> {

    fun existsByLocation(location: String): Mono<Boolean>

    @Query(
        """
            MATCH (p:Picture) WHERE p.id = $ pictureId 
            MATCH (t:Tag) WHERE t.id = $ tagId
            CREATE (p)-[:TAGGED_BY]->(t)
        """
    )
    fun addTag(pictureId: String, tagId: String): Mono<Void>

    @Query(
        """
            MATCH (p:Picture)-[rel:TAGGED_BY]->(t:Tag)
              WHERE p.id = $ pictureId AND t.id = $ tagId
            DELETE rel
        """,
        delete = true
    )
    fun removeTag(pictureId: String, tagId: String): Mono<Void>
}
