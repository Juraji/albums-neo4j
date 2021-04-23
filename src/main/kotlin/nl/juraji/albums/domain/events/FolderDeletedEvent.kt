package nl.juraji.albums.domain.events

data class FolderDeletedEvent(
    val folderId: String
) : AlbumEvent
