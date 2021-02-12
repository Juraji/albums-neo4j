package nl.juraji.albums.domain.directories

import nl.juraji.albums.domain.events.AlbumEvent

class DirectoryCreatedEvent(
    source: Any,
    val directoryId: String,
) : AlbumEvent(source)
