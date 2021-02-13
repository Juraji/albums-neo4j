package nl.juraji.albums.domain.directories

import nl.juraji.albums.domain.events.AlbumEvent

data class DirectoryCreatedEvent(
    val directoryId: String,
) : AlbumEvent
