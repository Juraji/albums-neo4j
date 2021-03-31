package nl.juraji.albums.domain.pictures

data class DuplicatedByView(
    val target: Picture,
    val similarity: Double,
)
