package nl.juraji.albums.domain.tags

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import reactor.core.publisher.Mono

interface TagsRepository : ReactiveNeo4jRepository<Tag, String> {

    @Suppress("SpringDataRepositoryMethodReturnTypeInspection")
    fun existsByLabel(label: String): Mono<Boolean>
}
