package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator

@Node
@Suppress("ArrayInDataClass")
data class HashData(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val hash: String,

    @Relationship("DESCRIBES")
    val picture: Picture,
)
