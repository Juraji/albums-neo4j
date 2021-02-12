package nl.juraji.albums.domain

import nl.juraji.albums.domain.duplicates.Duplicate
import nl.juraji.albums.domain.duplicates.DuplicateRepository
import nl.juraji.albums.domain.duplicates.DuplicatedBy
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime

@Service
class DuplicatesService(
    private val duplicateRepository: DuplicateRepository,
    private val pictureRepository: PictureRepository
) {

    fun findAllDuplicates(): Flux<Duplicate> = duplicateRepository.findAll()

    fun setDuplicatePicture(
        sourceId: String,
        targetId: String,
        similarity: Float,
        matchedOn: LocalDateTime = LocalDateTime.now()
    ): Mono<Duplicate> = validateAsync {
        val to = duplicateRepository.existsBySourceIdAndTargetId(sourceId,targetId)
        val from = duplicateRepository.existsBySourceIdAndTargetId(targetId,sourceId)

        isFalse(to) { "Duplicate ($sourceId->$targetId) is already registered" }
        isFalse(from) { "Duplicate ($sourceId->$targetId) is already registered" }
    }
        .flatMap {
            Mono.zip(
                pictureRepository.findById(sourceId),
                pictureRepository.findById(targetId)
            )
        }
        .map { (source, target) ->
            Duplicate(
                similarity = similarity,
                matchedOn = matchedOn,
                source = source,
                target = target
            )
        }
        .flatMap(duplicateRepository::save)

    fun findByPictureId(pictureId: String): Flux<DuplicatedBy> = duplicateRepository.findBySourceId(pictureId)

    fun deleteById(duplicateId: String): Mono<Void> = duplicateRepository.deleteById(duplicateId)
}
