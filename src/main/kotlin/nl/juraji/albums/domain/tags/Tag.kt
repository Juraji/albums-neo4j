package nl.juraji.albums.domain.tags

import nl.juraji.albums.util.validators.HexColor
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import javax.validation.constraints.NotBlank

data class Tag(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @field:NotBlank
    val label: String,
    @field:HexColor
    val textColor: String,
    @field:HexColor
    val color: String,
)
