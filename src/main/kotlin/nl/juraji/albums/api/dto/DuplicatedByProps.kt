package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.duplicates.DuplicatedBy
import java.time.LocalDateTime

data class DuplicatedByProps(
    val matchedOn: LocalDateTime,
    val similarity: Float,
    val target: PictureProps,
)

fun DuplicatedBy.toDuplicatedByProps(): DuplicatedByProps = DuplicatedByProps(
    matchedOn = matchedOn,
    similarity = similarity,
    target = target.toPictureProps(),
)
