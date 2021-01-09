package nl.juraji.albums.query.dto

import java.time.LocalDateTime

data class Duplicate(
    val matchedAt: LocalDateTime,
    val similarity: Double,
    val picture: Picture,
)
