package nl.juraji.albums.domain

import nl.juraji.albums.configuration.DuplicateScannerConfiguration
import nl.juraji.albums.domain.duplicates.Duplicate
import nl.juraji.albums.domain.duplicates.DuplicateRepository
import nl.juraji.albums.domain.duplicates.DuplicatedBy
import nl.juraji.albums.domain.pictures.PictureHash
import nl.juraji.albums.domain.pictures.PictureHashDataRepository
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime
import java.util.*

@Service
class DuplicatesService(
    private val duplicateRepository: DuplicateRepository,
    private val pictureRepository: PictureRepository,
    private val pictureHashDataRepository: PictureHashDataRepository,
    private val configuration: DuplicateScannerConfiguration
) {

    fun findAllDuplicates(): Flux<Duplicate> = duplicateRepository.findAll()

    fun setDuplicatePicture(
        sourceId: String,
        targetId: String,
        similarity: Float,
        matchedOn: LocalDateTime = LocalDateTime.now()
    ): Mono<Duplicate> = validateAsync {
        val to = duplicateRepository.existsBySourceIdAndTargetId(sourceId, targetId)
        val from = duplicateRepository.existsBySourceIdAndTargetId(targetId, sourceId)

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

    fun scanDuplicatesForPicture(pictureId: String): Flux<Duplicate> {
        val pictureHash = pictureHashDataRepository.findByPictureId(pictureId).share()
        val otherHashes = pictureHashDataRepository.findAll()

        return otherHashes
            .filterWhen { o -> pictureHash.map { p -> p.id != o.id } }
            .flatMap { target -> pictureHash.map { source -> compare(source, target) } }
            .filter { it.similarity >= configuration.similarityThreshold }
            .flatMap { setDuplicatePicture(pictureId, it.target.id!!, it.similarity) }
            .onErrorContinue { t, _ -> logger.info(t.localizedMessage) }
    }

    private fun compare(source: PictureHash, target: PictureHash): DuplicatedBy {
        val similarity = if (source.hash == target.hash) {
            1.0f
        } else {
            val decoder = Base64.getDecoder();
            val sourceHash = BitSet.valueOf(decoder.decode(source.hash))
            val targetHash = BitSet.valueOf(decoder.decode(target.hash))

            sourceHash.xor(targetHash)
            sourceHash.flip(0, sourceHash.length() - 1)

            val total = sourceHash.length().toFloat()
            val difference = sourceHash.cardinality().toFloat()
            difference / total
        }

        return DuplicatedBy(
            matchedOn = LocalDateTime.now(),
            similarity = similarity,
            target = target.picture
        )
    }

    companion object : LoggerCompanion(DuplicatesService::class)
}
