package nl.juraji.albums.domain.pictures

data class DuplicatesView(
    val sourceId: String,
    val targetId: String,
    val similarity: Double,
)
