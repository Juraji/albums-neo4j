package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.events.AlbumEvent
import org.neo4j.driver.internal.shaded.reactor.core.publisher.DirectProcessor
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import javax.annotation.PreDestroy

@Component
class SseEventListener {
    private val updatesProcessor: DirectProcessor<AlbumEvent> = DirectProcessor.create()

    @PreDestroy
    fun destroy() = updatesProcessor.onComplete()

    @EventListener
    fun onAlbumEvent(event: AlbumEvent) = updatesProcessor.onNext(event)

    fun getAlbumEventStream(): Flux<AlbumEvent> = updatesProcessor.toFlux()
}
