package nl.juraji.albums.domain.directories

import nl.juraji.albums.domain.events.AlbumEvent

class DirectoryDeletedEvent(
    source: Any,
    val directoryId: String
) : AlbumEvent(source)
