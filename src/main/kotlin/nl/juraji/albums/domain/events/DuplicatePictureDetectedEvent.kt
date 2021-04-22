package nl.juraji.albums.domain.events

import nl.juraji.albums.domain.pictures.DuplicatesView

data class DuplicatePictureDetectedEvent(
    val duplicate: DuplicatesView
): AlbumEvent
