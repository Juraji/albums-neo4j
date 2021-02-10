package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.PictureMetaDataService
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PictureMetaDataEventListener(
    private val pictureMetaDataService: PictureMetaDataService
) : ReactiveEventListener() {

    @EventListener
    fun updatePictureMetaData(event: PictureCreatedEvent) = consumePublisher {
        pictureMetaDataService.updateMetaData(event.pictureId)
    }

    @EventListener
    fun generatePictureImageHash(event: PictureCreatedEvent) = consumePublisher {
        pictureMetaDataService.updatePictureHash(event.pictureId)
    }
}
