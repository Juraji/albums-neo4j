package nl.juraji.albums.model

import nl.juraji.albums.util.Patterns
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Node
data class Tag(
    @Id
    val id: Long?,
    @field:NotBlank
    val label: String,
    @field:NotBlank
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val color: String
)
