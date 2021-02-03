package nl.juraji.albums.api.dto

import nl.juraji.albums.model.FileType
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.PictureDescription
import nl.juraji.albums.util.validators.ValidFileSystemPath
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

data class PictureDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    @field:ValidFileSystemPath
    val location: String,
    @field:NotBlank
    val name: String,
    @field:PositiveOrZero
    val fileSize: Long,
    val fileType: FileType,
    val lastModified: LocalDateTime,
)

fun Picture.toPictureDto() = PictureDto(
    id = id ?: "",
    location = location,
    name = name,
    fileSize = fileSize ?: 0,
    fileType = fileType ?: FileType.UNKNOWN,
    lastModified = lastModified ?: LocalDateTime.now(),
)

fun PictureDescription.toPictureDto() = PictureDto(
    id = id,
    location = location,
    name = name,
    fileSize = fileSize,
    fileType = fileType,
    lastModified = lastModified,
)
