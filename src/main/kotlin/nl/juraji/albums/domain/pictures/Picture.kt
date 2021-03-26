package nl.juraji.albums.domain.pictures

import com.fasterxml.jackson.annotation.JsonIgnore
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
    val type: FileType,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val addedOn: LocalDateTime = LocalDateTime.now(),
    @JsonIgnore
    val thumbnailLocation: String = "",
    @JsonIgnore
    val pictureLocation: String = "",
)

