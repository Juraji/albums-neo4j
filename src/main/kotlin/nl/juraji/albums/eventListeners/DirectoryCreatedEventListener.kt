package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DirectoryCreatedEventListener(
    private val directoryService: DirectoryService
) : ReactiveEventListener() {

    @EventListener
    fun linkToParentDirectory(event: DirectoryCreatedEvent) = consumePublisher {
        directoryService.findAndLinkParent(event.directoryId)
    }

    @EventListener
    fun linkToChildDirectories(event: DirectoryCreatedEvent) = consumePublisher {
        directoryService.findAndLinkChildren(event.directoryId)
    }
}
