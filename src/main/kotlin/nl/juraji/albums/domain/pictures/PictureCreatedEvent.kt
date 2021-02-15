package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

data class PictureCreatedEvent(
    val pictureId: String,
    val location: String,
    val directoryId: String,
) : AlbumEvent
