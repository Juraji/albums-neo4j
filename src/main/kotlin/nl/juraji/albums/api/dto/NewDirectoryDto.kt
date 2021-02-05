package nl.juraji.albums.api.dto

import nl.juraji.albums.util.validators.ValidFileSystemPath

data class NewDirectoryDto(
    @field:ValidFileSystemPath
    val location: String
)
