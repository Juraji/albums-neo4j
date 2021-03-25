package nl.juraji.albums.domain.folders

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import javax.validation.constraints.NotBlank

@Node
data class Folder(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @field:NotBlank
    val name: String,
)
