package nl.juraji.albums.api.dto

import nl.juraji.albums.util.validators.HexColor
import javax.validation.constraints.NotBlank

data class NewTagDto(
    @field:NotBlank
    val label: String,
    @field:HexColor
    val color: String,
    @field:HexColor
    val textColor: String,
)
