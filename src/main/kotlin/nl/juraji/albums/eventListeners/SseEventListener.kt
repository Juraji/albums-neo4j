package nl.juraji.albums.eventListeners

import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.util.LoggerCompanion
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import reactor.core.publisher.Sinks.EmitResult
import javax.annotation.PreDestroy

@Component
class SseEventListener {
    private val updatesProcessor: Sinks.Many<AlbumEvent> = Sinks.many().multicast().directAllOrNothing()

    @PreDestroy
    fun destroy() = updatesProcessor.emitComplete(FAIL_FAST)

    @EventListener
    fun onAlbumEvent(event: AlbumEvent) = updatesProcessor
        .emitNext(event) { _, result -> result == EmitResult.FAIL_NON_SERIALIZED }

    fun getAlbumEventStream(): Flux<AlbumEvent> = updatesProcessor.asFlux()

    companion object : LoggerCompanion(SseEventListener::class)
}
