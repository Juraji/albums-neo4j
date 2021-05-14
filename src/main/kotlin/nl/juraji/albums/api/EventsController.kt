package nl.juraji.albums.api

import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.util.kotlin.ServerSentEventFlux
import nl.juraji.albums.util.kotlin.toServerSentEvents
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Sinks
import javax.annotation.PreDestroy

@RestController
@RequestMapping("/events")
class EventsController {
    private val updatesProcessor: Sinks.Many<AlbumEvent> = Sinks.many().multicast().directAllOrNothing()

    @PreDestroy
    fun destroy() = updatesProcessor.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST)

    @Async
    @EventListener
    fun onAlbumEvent(event: AlbumEvent) = updatesProcessor
        .emitNext(event) { _, result -> result == Sinks.EmitResult.FAIL_NON_SERIALIZED }

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getAlbumEventStream(): ServerSentEventFlux<AlbumEvent> = updatesProcessor
        .asFlux()
        .toServerSentEvents()
}
