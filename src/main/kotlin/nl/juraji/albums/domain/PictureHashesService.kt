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
        .flatMap { hash ->
            pictureHashesRepository
                .findByPictureId(pictureId)
                .map { it.copy(data = hash) }
                .switchIfEmpty {
                    picturesService.getById(pictureId)
                        .map { p -> PictureHash(data = hash, picture = p) }
                }
        }
        .flatMap { pictureHashesRepository.save(it) }
        .doOnNext { applicationEventPublisher.publishEvent(PictureHashGeneratedEvent(pictureId)) }

    @Async
    @EventListener(PictureAddedEvent::class)
    fun generateHashOnPictureAdded(e: PictureAddedEvent) = consumePublisher {
        updatePictureHash(e.picture.id)
    }
}
