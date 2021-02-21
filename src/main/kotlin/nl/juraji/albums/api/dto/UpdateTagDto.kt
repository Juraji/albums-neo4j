package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.Patterns
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class UpdateTagDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val label: String,
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val color: String,
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val textColor: String,
)

fun UpdateTagDto.toTag(): Tag = Tag(
    id = id,
    label = label,
    color = color,
    textColor = textColor,
)
