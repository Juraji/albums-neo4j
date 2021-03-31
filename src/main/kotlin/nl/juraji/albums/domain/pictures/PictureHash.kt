package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.util.*

data class PictureHash(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val data: BitSet,
    @Relationship("DESCRIBES")
    val picture: Picture
)
