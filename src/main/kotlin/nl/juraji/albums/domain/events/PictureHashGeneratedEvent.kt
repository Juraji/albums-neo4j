package nl.juraji.albums.domain.events

data class PictureHashGeneratedEvent(
    private val pictureId: String
) : AlbumEvent
