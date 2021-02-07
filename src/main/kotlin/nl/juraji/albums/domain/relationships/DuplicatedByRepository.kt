package nl.juraji.albums.domain.relationships

import nl.juraji.albums.util.SimpleNeo4JRecordMapper
import nl.juraji.albums.util.toUnit
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.data.neo4j.core.fetchAs
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class DuplicatedByRepository(
    private val neo4jClient: ReactiveNeo4jClient,
) : SimpleNeo4JRecordMapper() {

    fun findByPictureId(pictureId: String): Flux<DuplicatedBy> = neo4jClient
        .query(
            """
                MATCH (:Picture {id: $ pictureId})-[root:DUPLICATED_BY]-(target:Picture)
                RETURN DISTINCT root{.matchedOn, .similarity}, target
            """
        )
        .bind(pictureId).to("pictureId")
        .fetchAs<DuplicatedBy>().mappedBy { _, rec -> mapToDataClass(rec) }
        .all()


    fun findAllDistinctDuplicatedBy(): Flux<DuplicatedByWithSource> = neo4jClient
        .query(
            """
                MATCH (source:Picture)-[root:DUPLICATED_BY]-(target:Picture)
                RETURN DISTINCT root{.matchedOn, .similarity}, source, target
            """
        )
        .fetchAs<DuplicatedByWithSource>().mappedBy { _, rec -> mapToDataClass(rec) }
        .all()

    fun addDuplicatedBy(
        sourceId: String,
        targetId: String,
        similarity: Double,
        matchedOn: LocalDateTime
    ): Mono<Unit> = neo4jClient
        .query(
            """
                MATCH (s:Picture) WHERE s.id = $ sourceId 
                MATCH (t:Picture) WHERE t.id = $ targetId
                CREATE (s)-[:DUPLICATED_BY {matchedOn: $ matchedOn, similarity: $ similarity}]->(t)
            """
        )
        .bind(sourceId).to("sourceId")
        .bind(targetId).to("targetId")
        .bind(similarity).to("similarity")
        .bind(matchedOn.toString()).to("matchedOn")
        .run().toUnit()

    fun removeDuplicatedBy(pictureId: String, targetId: String): Mono<Unit> = neo4jClient
        .query(
            """
                MATCH (p:Picture)-[rel:DUPLICATED_BY]-(t:Picture)
                    WHERE p.id = $ pictureId AND t.id = $ targetId
                DELETE rel                
            """
        )
        .bind(pictureId).to("pictureId")
        .bind(targetId).to("targetId")
        .run().toUnit()
}
