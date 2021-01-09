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

    fun save(picture: Picture): Mono<Picture> = writeAndReturn(
        """
            MERGE (n:Picture {
                location:     $ location,
                name:         $ name,
                fileSize:     $ fileSize,
                lastModified: $ lastModified
            })
            RETURN n
        """,
        pictureMapper.dtoToPropertiesMap(picture)
    ).map(pictureMapper::recordToDto)

    fun findAll(): Flux<Picture> = readMultiple(
        """
            MATCH (n:Picture)
            RETURN n
            ORDER BY n.location
        """
    ).map(pictureMapper::recordToDto)

    fun findByLocation(location: String): Mono<Picture> = readSingle(
        "MATCH (n:Picture {location: $ location}) RETURN n",
        mapOf("location" to location)
    ).map(pictureMapper::recordToDto)

    fun findByTag(label: String): Flux<Picture> = readMultiple(
        """
            MATCH (n:Picture)-[:TAGGED_BY]->(:Tag {label: $ label}) 
            RETURN n
            ORDER BY n.location
        """,
        mapOf("label" to label)
    ).map(pictureMapper::recordToDto)

    fun findDuplicatedByForLocation(location: String): Flux<Duplicate> = readMultiple(
        """
            MATCH (:Picture {location: $ location})-[rel:DUPLICATED_BY]-(p:Picture) 
            RETURN rel,p
            ORDER BY rel.similarity DESC
        """,
        mapOf("location" to location)
    ).map {
        val rel = it.get("rel")
        Duplicate(
            matchedAt = rel.get("matchedAt").asLocalDateTime(),
            similarity = rel.get("similarity").asDouble(),
            picture = pictureMapper.recordToDto(it, "p")
        )
    }

    fun addTaggedByRelation(pictureLocation: String, tagLabel: String): Mono<ResultSummary> = write(
        """
            MATCH (p:Picture {location: $ pictureLocation})
            MATCH (t:Tag {label: $ tagLabel})
            
            WITH p,t
            MERGE (p)-[rel:TAGGED_BY]->(t)
        """,
        mapOf(
            "pictureLocation" to pictureLocation,
            "tagLabel" to tagLabel,
        )
    )

    fun removeTaggedByRelation(pictureLocation: String, tagLabel: String): Mono<ResultSummary> = write(
        """
            MATCH (p:Picture {location: $ pictureLocation})
            MATCH (t:Tag {label: $ tagLabel})

            WITH p,t
            MATCH (p)-[rel:TAGGED_BY]->(t)
            DELETE rel
        """,
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
        """,
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
        """,
        mapOf(
            "sourceLocation" to sourcePictureLocation,
            "targetLocation" to targetPictureLocation,
        )
    )
}
