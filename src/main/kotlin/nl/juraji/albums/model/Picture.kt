package nl.juraji.albums.model

import nl.juraji.albums.model.relationships.PictureTaggedByTag
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime

@Node
data class Picture(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val location: String,
    val name: String,
    val fileSize: Long? = null,
    val fileType: FileType? = null,
    val lastModified: LocalDateTime? = null,
    @Relationship(PictureTaggedByTag.LABEL)
    val tags: List<PictureTaggedByTag> = emptyList()
)
