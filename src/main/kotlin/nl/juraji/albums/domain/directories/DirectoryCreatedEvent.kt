package nl.juraji.albums.domain.directories

import org.springframework.context.ApplicationEvent

class DirectoryCreatedEvent(
    source: Any,
    val directory: Directory
) : ApplicationEvent(source)
