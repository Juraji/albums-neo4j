package nl.juraji.albums.api.dto

import nl.juraji.albums.util.Patterns
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class NewTagDto(
    @field:NotBlank
    val label: String,
    @field:Pattern(regexp = Patterns.HEX_COLOR)
    val color: String,
)
