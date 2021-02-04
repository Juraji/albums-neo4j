package nl.juraji.albums.domain.relationships

import nl.juraji.albums.domain.tags.Tag
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
data class TaggedByTag(
    @TargetNode
    val tag: Tag,
    val addedOn: LocalDateTime = LocalDateTime.now()
){
    companion object{
        const val LABEL = "TAGGED_BY"
    }
}
