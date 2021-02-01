package nl.juraji.albums.model

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import javax.validation.constraints.NotBlank

@Node
data class Directory(
    @Id
    val id: Long?,
    @field:NotBlank
    val location: String
)
