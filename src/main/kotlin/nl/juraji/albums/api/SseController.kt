package nl.juraji.albums.api

import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.eventListeners.SseEventListener
import nl.juraji.albums.util.ServerSentEventFlux
import nl.juraji.albums.util.toServerSentEvents
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/events")
class SseController(
    private val sseEventListener: SseEventListener
) {

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getAlbumEventStream(): ServerSentEventFlux<AlbumEvent> = sseEventListener
        .getAlbumEventStream()
        .toServerSentEvents()
}
