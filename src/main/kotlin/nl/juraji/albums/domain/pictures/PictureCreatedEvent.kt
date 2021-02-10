package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

class PictureCreatedEvent(
    source: Any,
    pictureId: String,
    location: String,
) : PictureEvent(source, pictureId, location)
