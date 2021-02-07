package nl.juraji.albums.domain.events

import org.springframework.context.ApplicationEvent

abstract class AlbumEvent(source: Any) : ApplicationEvent(source)
