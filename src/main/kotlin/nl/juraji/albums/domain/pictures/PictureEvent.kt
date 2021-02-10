package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

abstract class PictureEvent(
    source: Any,
    val pictureId: String,
    val location: String,
) : AlbumEvent(source) {
}
