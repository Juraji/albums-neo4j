package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.PictureAddedEvent
import nl.juraji.albums.domain.events.PictureHashGeneratedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.PictureHash
import nl.juraji.albums.domain.pictures.PictureHashesRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.lang.IllegalArgumentException
import java.util.*

@Service
class PictureHashesService(
    private val pictureHashesRepository: PictureHashesRepository,
    private val picturesService: PicturesService,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : ReactiveEventListener() {

    fun updatePictureHash(pictureId: String): Mono<PictureHash> = picturesService
        .getPictureResource(pictureId)
        .flatMap(imageService::loadResourceAsImage)
        .flatMap(imageService::generateHash)
        .zipWith(getOrCreateForPicture(pictureId))
        .flatMap { (generatedData, pictureHash) -> pictureHashesRepository.save(pictureHash.copy(data = generatedData)) }
        .doOnNext { applicationEventPublisher.publishEvent(PictureHashGeneratedEvent(pictureId)) }

    @Async
    @EventListener(PictureAddedEvent::class)
    fun generateHashOnPictureAdded(e: PictureAddedEvent) = consumePublisher {
        updatePictureHash(e.picture.id)
    }

    private fun getOrCreateForPicture(pictureId: String): Mono<PictureHash> = pictureHashesRepository
        .findByPictureId(pictureId)
        .switchIfEmpty {
            picturesService.getById(pictureId)
                .map { PictureHash(data = BitSet(), picture = it) }
                .flatMap(pictureHashesRepository::save)
        }
}
