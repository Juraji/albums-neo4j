package nl.juraji.albums.domain.folders

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator

@Node
data class Folder(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val name: String,
)
