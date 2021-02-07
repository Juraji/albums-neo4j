package nl.juraji.albums.domain.pictures

import org.springframework.context.ApplicationEvent

class PictureCreatedEvent(
    source: Any,
    val picture: Picture
) : ApplicationEvent(source)
