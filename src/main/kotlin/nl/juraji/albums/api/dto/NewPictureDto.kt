package nl.juraji.albums.api.dto

import nl.juraji.albums.util.validators.ValidFileSystemPath
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class NewPictureDto(
    @field:NotBlank
    @field:ValidFileSystemPath
    val location: String,
    @field:Size(min = 1)
    val name: String?,
)
