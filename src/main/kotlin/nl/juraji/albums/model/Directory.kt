package nl.juraji.albums.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.juraji.albums.model.relationships.DirectoryContainsPicture
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import javax.validation.constraints.NotBlank

@Node
data class Directory(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @field:NotBlank
    val location: String,
    @JsonIgnore
    @Relationship(DirectoryContainsPicture.LABEL)
    val pictures: List<DirectoryContainsPicture> = emptyList()
)
