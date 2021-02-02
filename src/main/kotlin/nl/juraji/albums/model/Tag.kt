package nl.juraji.albums.model

import nl.juraji.albums.util.Patterns
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Node
data class Tag(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @field:NotBlank
    val label: String,
    @field:NotBlank
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val color: String
)
