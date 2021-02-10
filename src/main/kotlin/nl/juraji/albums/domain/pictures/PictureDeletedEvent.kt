package nl.juraji.albums.domain.pictures

class PictureDeletedEvent(
    source: Any,
    pictureId: String,
    location: String,
    val doDeleteFile: Boolean
) : PictureEvent(source, pictureId, location)
