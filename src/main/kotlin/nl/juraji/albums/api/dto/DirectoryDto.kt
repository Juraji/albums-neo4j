package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryDescription
import nl.juraji.albums.util.validators.ValidFileSystemPath
import javax.validation.constraints.NotBlank

data class DirectoryDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    @field:ValidFileSystemPath
    val location: String,
)

fun Directory.toDirectoryDto() = DirectoryDto(
    id = id ?: "",
    location = location
)

fun DirectoryDescription.toDirectoryDto() = DirectoryDto(
    id = id,
    location = location
)
