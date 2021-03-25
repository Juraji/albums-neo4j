package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime

@Node
data class Picture(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    val name: String,
    val type: FileType = FileType.UNKNOWN,
    val width: Int = 0,
    val height: Int = 0,
    val fileSize: Long = 0,
    val addedOn: LocalDateTime = LocalDateTime.now(),
    val lastModified: LocalDateTime = LocalDateTime.now()
)

