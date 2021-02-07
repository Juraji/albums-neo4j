package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.events.AlbumEvent

class PictureDeletedEvent(
    source: Any,
    val pictureId: Picture,
    val doDeleteFile: Boolean
) : AlbumEvent(source)
