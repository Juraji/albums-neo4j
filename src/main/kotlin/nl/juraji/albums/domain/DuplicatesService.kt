package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.toUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class DuplicatesService(
    private val pictureRepository: PictureRepository
) {

    fun setDuplicatePicture(
        sourceId: String,
        targetId: String,
        similarity: Double,
        matchedOn: LocalDateTime = LocalDateTime.now()
    ): Mono<Unit> = pictureRepository.addDuplicatedBy(sourceId, targetId, similarity, matchedOn).toUnit()

    fun unsetDuplicatePicture(
        pictureId: String,
        targetId: String
    ): Mono<Unit> = pictureRepository.removeDuplicatedBy(pictureId, targetId).toUnit()
}
