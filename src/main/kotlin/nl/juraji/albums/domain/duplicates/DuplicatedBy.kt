package nl.juraji.albums.domain.duplicates

import nl.juraji.albums.domain.pictures.Picture
import java.time.LocalDateTime

data class DuplicatedBy(
    val matchedOn: LocalDateTime,
    val similarity: Float,
    val target: Picture,
)
