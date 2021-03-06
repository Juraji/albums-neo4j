package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.PictureHashUpdatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PictureHashUpdatedEventListener(
    private val duplicatesService: DuplicatesService,
) : ReactiveEventListener() {

    @EventListener
    fun matchPictureDuplicates(event: PictureHashUpdatedEvent) = consumePublisher {
        duplicatesService.scanDuplicatesForPicture(event.pictureId)
    }
}
