package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.validators.HexColor
import javax.validation.constraints.NotBlank

data class UpdateTagDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val label: String,
    @field:HexColor
    val color: String,
    @field:HexColor
    val textColor: String,
)

fun UpdateTagDto.toTag(): Tag = Tag(
    id = id,
    label = label,
    color = color,
    textColor = textColor,
)
