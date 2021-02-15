package nl.juraji.albums.domain.directories

import nl.juraji.albums.domain.events.AlbumEvent

data class DirectoryTreeUpdatedEvent(
    val directoryId: String,
) : AlbumEvent
