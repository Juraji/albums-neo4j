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
              WHERE NOT rel.unlinked
            RETURN target.id AS targetId, source.id AS sourceId, rel.similarity AS similarity, rel.unlinked AS unlinked
        """
        )
        .fetch().all()
        .map(duplicatesViewMapper::mapFrom)

    fun findAllHistoric(): Flux<DuplicatesView> = neo4jClient
        .query(
            """
            MATCH (source:Picture)-[rel:DUPLICATED_BY]->(target:Picture)
              WHERE rel.unlinked
            RETURN target.id AS targetId, source.id AS sourceId, rel.similarity AS similarity, rel.unlinked AS unlinked
        """
        )
        .fetch().all()
        .map(duplicatesViewMapper::mapFrom)

    fun save(duplicatesView: DuplicatesView): Mono<DuplicatesView> = neo4jClient
        .query(
            """
            MATCH (source:Picture {id: $ sourceId})
            MATCH (target:Picture {id: $ targetId})
            WHERE NOT exists((source)-[:DUPLICATED_BY]-(target))    
    
            MERGE (source)-[rel:DUPLICATED_BY {similarity: $ similarity, unlinked: false}]-(target)
            RETURN target.id AS targetId, source.id AS sourceId, rel.similarity AS similarity, rel.unlinked AS unlinked
        """
        )
        .bind(duplicatesView.sourceId).to("sourceId")
        .bind(duplicatesView.targetId).to("targetId")
        .bind(duplicatesView.similarity).to("similarity")
        .fetch().one()
        .map(duplicatesViewMapper::mapFrom)

    fun removeAsDuplicate(sourceId: String, targetId: String): Mono<Void> = neo4jClient
        .query(
            """
            MATCH (:Picture {id: $ sourceId})-[rel:DUPLICATED_BY]->(:Picture {id: $ targetId})
            SET rel.unlinked = true
            """.trimIndent()
        )
        .bind(sourceId).to("sourceId")
        .bind(targetId).to("targetId")
        .run().flatMap { Mono.empty() }
}
