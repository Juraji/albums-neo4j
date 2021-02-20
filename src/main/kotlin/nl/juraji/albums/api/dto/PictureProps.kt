package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.tags.Tag
import java.time.LocalDateTime

data class PictureProps(
    val id: String,
    val location: String,
    val name: String,
    val width: Int = 0,
    val height: Int = 0,
    val fileSize: Long = 0,
    val fileType: FileType,
    val lastModified: LocalDateTime,
    val directory: DirectoryProps,
    val tags: List<Tag> = emptyList()
)

fun Picture.toPictureProps(): PictureProps = PictureProps(
    id = id ?: throw IllegalArgumentException("Picture id unknown"),
    location = location,
    name = name,
    width = width,
    height = height,
    fileSize = fileSize,
    fileType = fileType ?: FileType.UNKNOWN,
    lastModified = lastModified ?: LocalDateTime.MAX,
    directory = directory.toDirectoryProps(),
    tags = tags,
)
