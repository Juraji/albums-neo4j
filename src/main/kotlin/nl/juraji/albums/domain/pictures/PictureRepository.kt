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
        MATCH (p:Picture {id: $ pictureId})
        MATCH (t:Tag {id: $ tagId})
        MERGE (p)-[:TAGGED_BY]->(t)
        """
    )
    fun addTag(pictureId: String, tagId: String): Mono<Unit>

    @Query("MATCH (:Picture {id: $ pictureId})-[rel:TAGGED_BY]->(:Tag {id: $ tagId}) DELETE rel")
    fun removeTag(pictureId: String, tagId: String): Mono<Unit>

    @Query(
        """
            MATCH (root: Picture {id: $ id})
            OPTIONAL MATCH t=(root)<-[:DESCRIBES|HAS_TARGET|HAS_SOURCE*0..]-()
            DETACH DELETE t,root
        """, delete = true
    )
    fun deleteTreeById(id: String): Mono<Void>
}
