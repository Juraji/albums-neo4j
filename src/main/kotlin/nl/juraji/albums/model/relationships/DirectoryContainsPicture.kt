package nl.juraji.albums.model.relationships

import nl.juraji.albums.model.Picture
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
data class DirectoryContainsPicture(
    @TargetNode
    val picture: Picture,
    val addedOn: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val LABEL = "CONTAINS"
    }
}
