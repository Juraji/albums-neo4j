package nl.juraji.albums.domain.events

import nl.juraji.albums.domain.pictures.Picture

data class PictureAddedEvent(
    val folderId: String,
    val picture: Picture
): AlbumEvent
