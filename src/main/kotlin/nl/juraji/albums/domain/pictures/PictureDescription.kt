package nl.juraji.albums.domain.pictures

import java.time.LocalDateTime

data class PictureDescription(
    val id: String,
    val location: String,
    val name: String,
    val fileSize: Long,
    val fileType: FileType,
    val lastModified: LocalDateTime,
)
