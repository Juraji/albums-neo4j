package nl.juraji.albums.repositories

import nl.juraji.albums.model.Tag
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TagRepository: ReactiveNeo4jRepository<Tag, String> {
    fun existsByLabel(label: String): Mono<Boolean>
}
