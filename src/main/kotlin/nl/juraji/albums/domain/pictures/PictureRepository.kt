package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface PictureRepository : ReactiveNeo4jRepository<Picture, String> {

    fun existsByLocation(location: String): Mono<Boolean>

    @Query(
        """
            MATCH (s:Picture) WHERE s.id = $ sourceId 
            MATCH (t:Picture) WHERE t.id = $ targetId
            OPTIONAL MATCH (s)-[old_rel:DUPLICATED_BY]-(t)
            DELETE old_rel
            CREATE (s)-[:DUPLICATED_BY {matchedOn: $ matchedOn, similarity: $ similarity}]->(t)
        """
    )
    fun addDuplicatedBy(sourceId: String, targetId: String, similarity: Double, matchedOn: LocalDateTime): Mono<Unit>

    @Query(
        """
            MATCH (p:Picture)-[rel:DUPLICATED_BY]-(t:Picture)
                WHERE p.id = $ pictureId AND t.id = $ targetId
            DELETE rel
        """
    )
    fun removeDuplicatedBy(pictureId: String, targetId: String): Mono<Unit>
}
