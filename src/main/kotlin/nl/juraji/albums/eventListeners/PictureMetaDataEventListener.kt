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
    fun processNewPicture(event: PictureCreatedEvent) = consumePublisher {
        val metaData = pictureMetaDataService.updateMetaData(event.pictureId)
        val hash = pictureMetaDataService.updatePictureHash(event.pictureId)

        metaData.then(hash)
    }
}
