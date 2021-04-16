package nl.juraji.albums.domain.events

import nl.juraji.albums.domain.pictures.DuplicatesView

data class DuplicatePictureDetectedEvent(
    val sourceId: String,
    val targetId: String,
    val similarity: Double,
) {
    companion object {
        fun ofDuplicatesView(v: DuplicatesView) = DuplicatePictureDetectedEvent(
            sourceId = v.source.id!!,
            targetId = v.target.id!!,
            similarity = v.similarity
        )
    }
}
