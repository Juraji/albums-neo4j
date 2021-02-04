package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PictureRepository : ReactiveNeo4jRepository<Picture, String> {

    fun existsByLocation(location: String): Mono<Boolean>

    fun findDescriptionById(id: String): Mono<PictureDescription>

    // language=cypher
    @Query("MATCH (p:Picture) RETURN p")
    fun findAllDescriptions(): Flux<PictureDescription>

    // language=cypher
    @Query("""
        MATCH (p:Picture)-[rel:TAGGED_BY]->(t:Tag)
          WHERE p.id = $ pictureId AND t.id = $ tagId
        DELETE rel
    """, delete = true)
    fun removeTaggedByTag(pictureId: String, tagId: String): Mono<Void>

    // language=cypher
    @Query("""
        MATCH (p:Picture)-[rel:DUPLICATED_BY]->(t:Picture)
          WHERE p.id = $ pictureId AND t.id = $ targetId
        DELETE rel
    """, delete = true)
    fun removeDuplicatedBy(pictureId: String, targetId: String): Mono<Void>
}
