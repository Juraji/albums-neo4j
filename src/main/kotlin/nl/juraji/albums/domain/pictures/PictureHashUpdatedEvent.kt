package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

data class PictureHashUpdatedEvent(
    val pictureId: String
) : AlbumEvent
