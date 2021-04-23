package nl.juraji.albums.api.dto

import nl.juraji.albums.domain.folders.Folder
import nl.juraji.albums.domain.pictures.Picture

data class PictureContainerDto(
    val picture: Picture,
    val folder: Folder
)
