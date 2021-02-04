package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.Patterns
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class TagDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val label: String,
    @field:NotBlank
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val color: String
)

fun Tag.toTagDto() = TagDto(
    id = id ?: "",
    label = label,
    color = color,
)
