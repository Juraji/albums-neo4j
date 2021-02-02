package nl.juraji.albums.model.relationships

import nl.juraji.albums.model.Tag
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
data class PictureTaggedByTag(
    @TargetNode
    val tag:Tag,
    val addedOn: LocalDateTime = LocalDateTime.now()
){
    companion object{
        const val LABEL = "TAGGED_BY"
    }
}
