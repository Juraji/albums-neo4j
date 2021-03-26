package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.support.UUIDStringGenerator

data class HashData(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val data: String
)
