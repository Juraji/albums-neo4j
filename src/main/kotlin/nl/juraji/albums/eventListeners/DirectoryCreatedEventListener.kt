package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.util.toPath
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DirectoryCreatedEventListener(
    private val directoryRepository: DirectoryRepository,
) : ReactiveEventListener() {

    @EventListener
    fun linkToParentDirectory(event: DirectoryCreatedEvent) = consumePublisher {
        val parentLocation = event.directory.location.toPath().parent.toString()
        directoryRepository.findByLocation(parentLocation)
            .flatMap { parent -> directoryRepository.addChild(parent.id!!, event.directory.id!!) }
    }

    @EventListener
    fun linkToChildDirectories(event: DirectoryCreatedEvent) = consumePublisher {
        val path = event.directory.location.toPath()
        directoryRepository.findByLocationStartingWith(event.directory.location)
            .filter { it.location.toPath().count() == (path.count() + 1) } // Only to direct children
            .flatMap { child -> directoryRepository.addChild(event.directory.id!!, child.id!!) }
    }
}
