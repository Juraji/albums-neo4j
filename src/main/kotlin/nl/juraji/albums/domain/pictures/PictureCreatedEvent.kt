package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

class PictureCreatedEvent(
    source: Any,
    val picture: Picture
) : AlbumEvent(source)
