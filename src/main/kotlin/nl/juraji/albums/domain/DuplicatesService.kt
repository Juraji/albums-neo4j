package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.domain.relationships.DuplicatedBy
import nl.juraji.albums.domain.relationships.DuplicatedByRepository
import nl.juraji.albums.domain.relationships.DuplicatedByWithSource
import nl.juraji.albums.util.toUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class DuplicatesService(
    private val duplicatedByRepository: DuplicatedByRepository
) {

    fun findDuplicatedByByPictureId(pictureId: String): Flux<DuplicatedBy> =
        duplicatedByRepository.findByPictureId(pictureId)

    fun findAllDistinctDuplicatedBy(): Flux<DuplicatedByWithSource> =
        duplicatedByRepository.findAllDistinctDuplicatedBy()

    fun setDuplicatePicture(
        sourceId: String,
        targetId: String,
        similarity: Double,
        matchedOn: LocalDateTime = LocalDateTime.now()
    ): Mono<Unit> = duplicatedByRepository.addDuplicatedBy(sourceId, targetId, similarity, matchedOn)

    fun unsetDuplicatePicture(
        pictureId: String,
        targetId: String
    ): Mono<Unit> = duplicatedByRepository.removeDuplicatedBy(pictureId, targetId)
}
