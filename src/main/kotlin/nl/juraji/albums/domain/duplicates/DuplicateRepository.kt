package nl.juraji.albums.domain.duplicates

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DuplicateRepository : ReactiveNeo4jRepository<Duplicate, String> {
    fun findBySourceId(pictureId: String): Flux<DuplicatedBy>

    fun existsBySourceIdAndTargetId(sourceId: String, targetId: String): Mono<Boolean>
}
