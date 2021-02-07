package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent
import org.springframework.context.ApplicationEvent

class PictureCreatedEvent(
    source: Any,
    val picture: Picture
) : AlbumEvent(source)
