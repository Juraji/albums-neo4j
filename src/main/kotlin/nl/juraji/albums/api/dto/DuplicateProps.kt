package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.duplicates.Duplicate
import java.time.LocalDateTime

data class DuplicateProps(
    val id: String,
    val matchedOn: LocalDateTime,
    val similarity: Float,
    val sourceId: String,
    val targetId: String,
)

fun Duplicate.toDuplicateProps(): DuplicateProps = DuplicateProps(
    id = id ?: throw IllegalArgumentException("Duplicate id unknown"),
    matchedOn = matchedOn,
    similarity = similarity,
    sourceId = source.id ?: throw IllegalArgumentException("Duplicate source id unknown"),
    targetId = target.id ?: throw IllegalArgumentException("Duplicate target id unknown"),
)
