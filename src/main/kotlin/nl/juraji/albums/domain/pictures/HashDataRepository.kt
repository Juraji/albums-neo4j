package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface HashDataRepository : ReactiveNeo4jRepository<HashData, String> {
    fun findByPictureId(pictureId: String): Mono<HashData>
}
