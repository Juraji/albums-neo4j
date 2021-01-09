package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Duplicate
import nl.juraji.albums.query.dto.Picture
import nl.juraji.albums.util.Neo4jDtoMapper
import org.neo4j.driver.Driver
import org.neo4j.driver.summary.ResultSummary
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class PictureRepository(driver: Driver) : Repository(driver) {
    private val pictureMapper = Neo4jDtoMapper(Picture::class)
    private val duplicateMapper = Neo4jDtoMapper(Duplicate::class)

    fun save(picture: Picture): Mono<Picture> = writeAndReturn(
        """
            MERGE (n:Picture {
                location:     $ location,
                name:         $ name,
                fileSize:     $ fileSize,
                lastModified: $ lastModified
            })
            RETURN n
        """.trimIndent(),
        pictureMapper.dtoToPropertiesMap(picture)
    ).map(pictureMapper::entityToDto)

    fun findAll(): Flux<Picture> = readMultiple(
        """
            MATCH (n:Picture)
            RETURN n
            ORDER BY n.location
        """.trimIndent()
    ).map(pictureMapper::entityToDto)

    fun findByLocation(location: String): Mono<Picture> = readSingle(
        "MATCH (n:Picture {location: $ location}) RETURN n",
        mapOf("location" to location)
    ).map(pictureMapper::entityToDto)

    fun findByTag(label: String): Flux<Picture> = readMultiple(
        """
            MATCH (n:Picture)-[:TAGGED_BY]->(:Tag {label: $ label}) 
            RETURN n
            ORDER BY n.location
        """.trimIndent(),
        mapOf("label" to label)
    ).map(pictureMapper::entityToDto)

    fun findDuplicatedByForLocation(location: String): Flux<Duplicate> = readMultiple(
        """
            MATCH (:Picture {location: $ location})-[rel:DUPLICATED_BY]-(p:Picture) 
            RETURN rel,p
            ORDER BY rel.similarity DESC
        """.trimIndent(),
        mapOf("location" to location)
    ).map {
        val rel = it.get("rel")
        Duplicate(
            matchedAt = rel.get("matchedAt").asLocalDateTime(),
            similarity = rel.get("similarity").asDouble(),
            picture = pictureMapper.entityToDto(it, "p")
        )
    }

    fun addTaggedByRelation(pictureLocation: String, tagLabel: String): Mono<ResultSummary> = write(
        """
            MATCH (p:Picture {location: $ pictureLocation})
            MATCH (t:Tag {label: $ tagLabel})
            
            WITH p,t
            MERGE (p)-[rel:TAGGED_BY]->(t)
        """.trimIndent(),
        mapOf(
            "pictureLocation" to pictureLocation,
            "tagLabel" to tagLabel,
        )
    )

    fun removeTaggedByRelation(pictureLocation: String, tagLabel: String): Mono<ResultSummary> = write(
        """
            MATCH (p:Picture {location: ${'$'} pictureLocation})
            MATCH (t:Tag {label: ${'$'} tagLabel})
            
            WITH p,t
            MATCH (p)-[rel:TAGGED_BY]->(t)
            DELETE rel
        """.trimIndent(),
        mapOf(
            "pictureLocation" to pictureLocation,
            "tagLabel" to tagLabel,
        )
    )

    fun addDuplicatedByRelation(
        sourcePictureLocation: String,
        targetPictureLocation: String,
        matchedAt: LocalDateTime,
        similarity: Double
    ): Mono<ResultSummary> = write(
        """
            MATCH (source:Picture {location: $ sourceLocation})
            MATCH (target:Picture {location: $ targetLocation})
            
            WITH source,target
            MERGE (source)-[:DUPLICATED_BY {matchedAt: $ matchedAt, similarity: $ similarity}]-(target)
        """.trimIndent(),
        mapOf(
            "sourceLocation" to sourcePictureLocation,
            "targetLocation" to targetPictureLocation,
            "matchedAt" to matchedAt,
            "similarity" to similarity,
        )
    )

    fun removeDuplicatedByRelation(
        sourcePictureLocation: String,
        targetPictureLocation: String
    ): Mono<ResultSummary> = write(
        """
            MATCH (source:Picture {location: $ sourceLocation})
            MATCH (target:Picture {location: $ targetLocation})
            
            WITH source,target
            MATCH (source)-[rel:DUPLICATED_BY]->(target)
            DELETE rel
        """.trimIndent(),
        mapOf(
            "sourceLocation" to sourcePictureLocation,
            "targetLocation" to targetPictureLocation,
        )
    )
}
