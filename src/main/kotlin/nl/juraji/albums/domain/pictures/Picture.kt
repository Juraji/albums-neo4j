package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.directories.Directory
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime

@Node
data class Picture(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val location: String,
    val name: String,
    val width: Int = 0,
    val height: Int = 0,
    val fileSize: Long = 0,
    val fileType: FileType? = null,
    val lastModified: LocalDateTime? = null,
    @Relationship("LOCATED_IN")
    val directory: Directory
)
