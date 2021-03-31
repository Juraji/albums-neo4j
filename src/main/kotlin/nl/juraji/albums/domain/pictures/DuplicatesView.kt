package nl.juraji.albums.domain.pictures

data class DuplicatesView(
    val source: Picture,
    val target: Picture,
    val similarity: Double,
)
