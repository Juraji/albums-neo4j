package nl.juraji.albums.domain.relationships

import nl.juraji.albums.util.CustomNeo4jRepository
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.data.neo4j.core.fetchAs
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class DuplicatedByRepository(
    private val neo4jClient: ReactiveNeo4jClient
) : CustomNeo4jRepository() {

    fun findByPictureId(pictureId: String): Flux<DuplicatedBy> = neo4jClient
        .query(
            """
                MATCH (:Picture {id: $ pictureId})-[root:DUPLICATED_BY]-(picture:Picture)
                RETURN DISTINCT root{.matchedOn, .similarity}, target
            """
        )
        .bind(pictureId).to("pictureId")
        .fetchAs<DuplicatedBy>().mappedBy { _, rec -> autoMapDataClass(rec) }
        .all()
}
