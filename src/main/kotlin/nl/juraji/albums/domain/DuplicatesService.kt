package nl.juraji.albums.domain

import nl.juraji.albums.configuration.ImageServiceConfiguration
import nl.juraji.albums.domain.events.DuplicatePictureDetectedEvent
import nl.juraji.albums.domain.events.PictureHashGeneratedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.LoggerCompanion
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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

    fun scanDuplicates(pictureId: String): Flux<DuplicatesView> {
        val pictureHash = pictureHashesRepository.findByPictureId(pictureId).share()
        val otherHashes = pictureHashesRepository.findAll()

        return otherHashes
            .filterWhen { o -> pictureHash.map { p -> p.id != o.id } }
            .flatMap { target -> pictureHash.map { source -> compare(source, target) } }
            .filter { (_, similarity) -> similarity >= configuration.similarityThreshold }
            .doOnNext { (target, similarity) ->
                applicationEventPublisher.publishEvent(
                    DuplicatePictureDetectedEvent(
                        sourceId = pictureId,
                        targetId = target.id!!,
                        similarity = similarity
                    )
                )
            }
            .flatMap { (target, similarity) ->
                pictureDuplicatesRepository.setAsDuplicate(
                    sourceId = pictureId,
                    targetId = target.id!!,
                    similarity = similarity,
                )
            }
            .onErrorContinue { t, _ -> logger.info(t.localizedMessage) }
    }

    @EventListener(PictureHashGeneratedEvent::class)
    fun scanDuplicatesOnPictureHashGenerated(e: PictureHashGeneratedEvent) = consumePublisher {
        scanDuplicates(e.pictureId)
    }

    private fun compare(source: PictureHash, target: PictureHash): Pair<Picture, Double> =
        target.picture to if (source.data == target.data) {
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
