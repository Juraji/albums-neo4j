package nl.juraji.albums.domain.relationships

import nl.juraji.albums.domain.pictures.Picture
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
data class DuplicatedByPicture(
    @TargetNode val picture: Picture,
    val matchedOn: LocalDateTime,
    val similarity: Double
)
