package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Tag
import nl.juraji.albums.util.Neo4jDtoMapper
import org.neo4j.driver.Driver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagRepository(driver: Driver) : Repository(driver) {
    private val mapper: Neo4jDtoMapper<Tag> = Neo4jDtoMapper(Tag::class)

    fun findByLabel(label: String): Mono<Tag> = readSingle(
        "MATCH (n:Tag {label: $ label}) RETURN n",
        mapOf("label" to label)
    ).map(mapper::entityToDto)

    fun findAll(): Flux<Tag> = readMultiple(
        "MATCH (n:Tag) RETURN n",
    ).map(mapper::entityToDto)

    fun save(tag: Tag): Mono<Tag> = writeAndReturn(
        """
            MERGE (n:Tag {label: $ label, color: $ color})
            WHERE n.label=$ label
            RETURN n
        """.trimIndent(),
        mapper.dtoToPropertiesMap(tag)
    ).map(mapper::entityToDto)
}
