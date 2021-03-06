package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.PictureHashService
import nl.juraji.albums.domain.PictureMetaDataService
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PictureMetaDataEventListener(
    private val pictureMetaDataService: PictureMetaDataService,
    private val pictureHashService: PictureHashService
) : ReactiveEventListener() {

    @EventListener
    fun processNewPicture(event: PictureCreatedEvent) = consumePublisher {
        val metaData = pictureMetaDataService.updateMetaData(event.pictureId)
        val hash = pictureHashService.updatePictureHash(event.pictureId)

        metaData.then(hash)
    }
}
