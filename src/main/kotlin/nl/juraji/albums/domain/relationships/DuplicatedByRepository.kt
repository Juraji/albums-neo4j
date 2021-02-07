package nl.juraji.albums.domain.relationships

import nl.juraji.albums.util.SimpleNeo4JRecordMapper
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.data.neo4j.core.fetchAs
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

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
}