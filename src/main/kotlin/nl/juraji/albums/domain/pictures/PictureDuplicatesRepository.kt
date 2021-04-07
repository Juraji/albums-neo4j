package nl.juraji.albums.domain.pictures

import nl.juraji.albums.util.Neo4jDataClassMapper
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PictureDuplicatesRepository(
    private val neo4jClient: ReactiveNeo4jClient
) {
    private val duplicatesViewMapper = Neo4jDataClassMapper(DuplicatesView::class)

    fun findAll(): Flux<DuplicatesView> = neo4jClient
        .query(
            """
            MATCH (source:Picture)-[rel:DUPLICATED_BY]->(target:Picture)
            RETURN target, source, rel.similarity AS similarity
        """
        )
        .fetch().all()
        .map(duplicatesViewMapper::mapFrom)

    fun setAsDuplicate(sourceId: String, targetId: String, similarity: Double): Mono<DuplicatesView> = neo4jClient
        .query(
            """
            MATCH (source:Picture {id: $ sourceId})
            MATCH (target:Picture {id: $ targetId})
    
            MERGE (source)-[rel:DUPLICATED_BY {similarity: $ similarity}]-(target)
            RETURN target, source, rel.similarity AS similarity
        """
        )
        .bind(sourceId).to("sourceId")
        .bind(targetId).to("targetId")
        .bind(similarity).to("similarity")
        .fetch().one()
        .map(duplicatesViewMapper::mapFrom)

    fun removeAsDuplicate(sourceId: String, targetId: String): Mono<Void> = neo4jClient
        .query(
            """
            MATCH (:Picture {id: $ sourceId})-[rel:DUPLICATED_BY]->(:Picture {id: $ targetId})
            DELETE rel
        """
        )
        .bind(sourceId).to("sourceId")
        .bind(targetId).to("targetId")
        .run().flatMap { Mono.empty() }
}
