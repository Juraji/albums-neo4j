package nl.juraji.albums.domain.relationships

import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class DuplicatedByRepository(
    private val neo4jClient: Neo4jClient
) {

    fun findByPictureId(pictureId: String): Flux<DuplicatedByPicture> {
        neo4jClient.query(
            "MATCH"
        )

        TODO()
    }
}
