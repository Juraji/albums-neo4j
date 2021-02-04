package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.relationships.DuplicatedByPicture
import nl.juraji.albums.domain.relationships.TaggedByTag
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

    @Relationship(TaggedByTag.LABEL)
    val tags: List<TaggedByTag> = emptyList(),
    @Relationship(DuplicatedByPicture.LABEL)
    val duplicates: List<DuplicatedByPicture> = emptyList()
)
