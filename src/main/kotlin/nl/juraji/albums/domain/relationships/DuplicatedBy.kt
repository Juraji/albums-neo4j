package nl.juraji.albums.domain.relationships

import nl.juraji.albums.domain.pictures.Picture
import java.time.LocalDateTime

data class DuplicatedBy(
    val matchedOn: LocalDateTime,
    val similarity: Double,
    val picture: Picture
)
