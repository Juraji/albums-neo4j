package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

data class PictureDeletedEvent(
    val pictureId: String,
    val location: String,
    val doDeleteFile: Boolean
) : AlbumEvent
