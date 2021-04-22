package nl.juraji.albums.domain

import nl.juraji.albums.configuration.ImageServiceConfiguration
import nl.juraji.albums.domain.events.DuplicatePictureDetectedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.DuplicatesView
import nl.juraji.albums.domain.pictures.PictureDuplicatesRepository
import nl.juraji.albums.domain.pictures.PictureHash
import nl.juraji.albums.domain.pictures.PictureHashesRepository
import nl.juraji.albums.util.kotlin.LoggerCompanion
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.*

@Service
class DuplicatesService(
    private val pictureHashesRepository: PictureHashesRepository,
    private val pictureDuplicatesRepository: PictureDuplicatesRepository,
    private val configuration: ImageServiceConfiguration,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ReactiveEventListener() {

    fun getAll(): Flux<DuplicatesView> =
        pictureDuplicatesRepository.findAll()

    fun removeDuplicate(pictureId: String, duplicateId: String): Mono<Void> =
        pictureDuplicatesRepository.removeAsDuplicate(pictureId, duplicateId)

    fun scanDuplicates(): Flux<DuplicatesView> =
        pictureHashesRepository.findAll()
            .collectList()
            .flatMapMany { sourceHashes -> mapHashCombinations(sourceHashes).toFlux() }
            .map { (source, target) -> DuplicatesView(source.picture.id!!, target.picture.id!!, compare(source, target)) }
            .filter { it.similarity >= configuration.similarityThreshold }
            .flatMap { pictureDuplicatesRepository.save(it) }
            .doOnNext { applicationEventPublisher.publishEvent(DuplicatePictureDetectedEvent(it)) }

    /**
     * Hash objects are removed from the source list as they are added to the output pairs,
     * this makes all output pairs unique.
     */
    private fun mapHashCombinations(sourceHashes: List<PictureHash>): List<Pair<PictureHash, PictureHash>> {
        val availableHashes = sourceHashes.toMutableList()
        return sourceHashes.flatMap { sourceHash ->
            availableHashes.remove(sourceHash)
            availableHashes.map { sourceHash to it }
        }
    }

    private fun compare(source: PictureHash, target: PictureHash): Double =
        if (source.data == target.data) {
            1.0
        } else {
            val sourceHash = source.data.clone() as BitSet

            sourceHash.xor(target.data)
            sourceHash.flip(0, sourceHash.length() - 1)

            val total = sourceHash.length().toDouble()
            val difference = sourceHash.cardinality().toDouble()
            difference / total
        }

    companion object : LoggerCompanion(DuplicatesService::class)
}
