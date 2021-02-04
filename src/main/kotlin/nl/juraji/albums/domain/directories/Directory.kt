package nl.juraji.albums.domain.directories

import nl.juraji.albums.domain.relationships.ContainsPicture
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator

@Node
data class Directory(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val location: String,
    @Relationship(ContainsPicture.LABEL)
    val pictures: List<ContainsPicture> = emptyList()
)
