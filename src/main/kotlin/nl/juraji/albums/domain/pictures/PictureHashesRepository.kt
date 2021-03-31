package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PictureHashesRepository : ReactiveNeo4jRepository<PictureHash, String> {
    fun findByPictureId(pictureId: String): Mono<PictureHash>
}
