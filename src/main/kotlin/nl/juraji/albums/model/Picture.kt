package nl.juraji.albums.model

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

@Node
data class Picture(
    @Id
    val id: Long?,
    @field:NotBlank
    val location: String,
    @field:NotBlank
    val name: String,
    @field:PositiveOrZero
    val fileSize: Long,
    val lastModified: LocalDateTime
)
