package nl.juraji.albums.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.juraji.albums.model.relationships.PictureTaggedByTag
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

@Node
data class Picture(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @field:NotBlank
    val location: String,
    @field:NotBlank
    val name: String,
    @field:PositiveOrZero
    val fileSize: Long?,
    val fileType: FileType?,
    val lastModified: LocalDateTime?,
    @JsonIgnore
    @Relationship(PictureTaggedByTag.LABEL)
    val tags: List<PictureTaggedByTag> = emptyList()
)
