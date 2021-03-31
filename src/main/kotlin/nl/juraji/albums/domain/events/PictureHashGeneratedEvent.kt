package nl.juraji.albums.domain.events

data class PictureHashGeneratedEvent(
    val pictureId: String
) : AlbumEvent
