package nl.juraji.albums.domain.pictures

class PictureCreatedEvent(
    source: Any,
    pictureId: String,
    location: String,
) : PictureEvent(source, pictureId, location)
