package nl.juraji.albums.domain.events

data class FolderCreatedEvent(
    val folderId: String,
    val name: String,
    val parentId: String
) : AlbumEvent
