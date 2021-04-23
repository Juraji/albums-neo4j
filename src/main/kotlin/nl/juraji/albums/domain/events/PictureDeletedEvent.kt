package nl.juraji.albums.domain.events

data class PictureDeletedEvent(
    val pictureId: String
): AlbumEvent
