package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@Node
data class Picture(
    @Id
    val id: String,
    @field:NotBlank
    val name: String,
    val type: FileType,
    val width: Int = 0,
    val height: Int = 0,
    val fileSize: Long = 0,
    val addedOn: LocalDateTime = LocalDateTime.now(),
    val lastModified: LocalDateTime = LocalDateTime.now(),
)

