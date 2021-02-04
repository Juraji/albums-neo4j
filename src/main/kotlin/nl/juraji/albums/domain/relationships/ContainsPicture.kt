package nl.juraji.albums.domain.relationships

import nl.juraji.albums.domain.pictures.Picture
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
data class ContainsPicture(
    @TargetNode
    val picture: Picture,
    val addedOn: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val LABEL = "CONTAINS"
    }
}
