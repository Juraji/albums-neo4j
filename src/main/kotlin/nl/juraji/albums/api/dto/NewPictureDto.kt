package nl.juraji.albums.api.dto

import nl.juraji.albums.util.validators.ValidFileSystemPath
import javax.validation.constraints.NotBlank

data class NewPictureDto(
    @field:NotBlank
    @field:ValidFileSystemPath
    val location: String,
    @field:NotBlank
    val name: String?,
)
