package nl.juraji.albums.domain.duplicates

import nl.juraji.albums.domain.pictures.Picture
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime

@Node
data class Duplicate(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val matchedOn: LocalDateTime,
    val similarity: Float,

    @Relationship("HAS_SOURCE")
    val source: Picture,
    @Relationship("HAS_TARGET")
    val target: Picture
)
