package nl.juraji.albums.model

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator

@Node
data class Tag(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val label: String,
    val color: String
)
