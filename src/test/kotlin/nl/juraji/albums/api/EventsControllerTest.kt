package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.events.AlbumEvent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier

@WebFluxTest(EventsController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class EventsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Test
    fun getAlbumEventStream() {
        val streamData: List<TestEvent> = fixture.next()

        val returnResult: FluxExchangeResult<ServerSentEvent<TestEvent>> = webTestClient.get()
            .uri("/events")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        val resultFlux = returnResult.responseBody
            .filter { it.event() != "ping" }
            .map { it.data()!! }

        streamData.forEach(applicationEventPublisher::publishEvent)

        StepVerifier.create(resultFlux)
            .expectNextSequence(streamData)
            .thenCancel()
            .verify()
    }

    internal data class TestEvent(val value: String) : AlbumEvent
}
