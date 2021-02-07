package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.pictures.PictureDeletedEvent
import nl.juraji.albums.util.toPath
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PictureDeletedEventListener(
    private val fileOperations: FileOperations
) : ReactiveEventListener() {

    @EventListener(condition = "#event.doDeleteFile")
    fun deletePictureImageFile(event: PictureDeletedEvent) = handleAsMono {
        fileOperations.deleteIfExists(event.pictureId.location.toPath())
    }

}
