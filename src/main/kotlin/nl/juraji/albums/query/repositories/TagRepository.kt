package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Tag
import nl.juraji.albums.util.Neo4jDtoMapper
import org.neo4j.driver.Driver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class TagRepository(
    private val driver: Driver
) {
    private val mapper: Neo4jDtoMapper<Tag> = Neo4jDtoMapper(Tag::class)

    fun findByLabel(label: String): Mono<Tag> = driver.rxSession()
        .readTransaction {
            it.run(
                "MATCH (n:Tag {label: \$label}) RETURN n",
                mapOf("label" to label)
            ).records()
        }
        .toMono()
        .map(mapper::recordToDto)

    fun findAll(): Flux<Tag> = driver.rxSession()
        .readTransaction { it.run("MATCH (n:Tag) RETURN n").records() }
        .toFlux()
        .map(mapper::recordToDto)

    fun save(tag: Tag): Mono<Tag> {
        return driver.rxSession()
            .writeTransaction {
                it.run(
                    """
                    MERGE (n:Tag {
                        label: ${'$'}label,
                        color: ${'$'}color
                    })
                    """.trimIndent(),
                    mapOf(
                        "label" to tag.label,
                        "color" to tag.color
                    )
                ).records()
            }
            .toMono()
            .map(mapper::recordToDto)
    }
}
