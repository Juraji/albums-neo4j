package nl.juraji.albums.domain.events

data class DuplicatePictureDetectedEvent(
    val sourceId: String,
    val targetId: String,
    val similarity: Double,
)
