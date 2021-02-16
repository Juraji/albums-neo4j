package nl.juraji.albums.domain.pictures

import java.time.LocalDateTime

data class PictureProps(
    val id: String,
    val location: String,
    val name: String,
    val width: Int = 0,
    val height: Int = 0,
    val fileSize: Long = 0,
    val fileType: FileType,
    val lastModified: LocalDateTime,
)
