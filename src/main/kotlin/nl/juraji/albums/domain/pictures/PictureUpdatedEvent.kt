package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

data class PictureUpdatedEvent(
    val pictureId: String
) : AlbumEvent
