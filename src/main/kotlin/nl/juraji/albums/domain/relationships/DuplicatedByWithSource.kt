package nl.juraji.albums.domain.relationships

import nl.juraji.albums.domain.pictures.Picture
import java.time.LocalDateTime

data class DuplicatedByWithSource(
    val matchedOn: LocalDateTime,
    val similarity: Double,
    val source: Picture,
    val target: Picture,
)
