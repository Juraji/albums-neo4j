package nl.juraji.albums.query.dto

import java.time.LocalDateTime

data class Picture(
    val location: String,
    val name: String,
    val fileSize: Long,
    val lastModified: LocalDateTime
)
