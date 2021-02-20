package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.directories.Directory

data class DirectoryProps(
    val id: String? = null,
    val location: String,
    val name: String,
)

fun Directory.toDirectoryProps(): DirectoryProps = DirectoryProps(
    id = id ?: throw IllegalArgumentException("Directory id unknown"),
    location = location,
    name = name
)
