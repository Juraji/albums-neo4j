package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.eventListeners.SseEventListener
import nl.juraji.albums.util.returnsFluxOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier

@WebFluxTest(SseController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class SseControllerTest {

    @MockkBean
    private lateinit var sseEventListener: SseEventListener

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    fun getAlbumEventStream() {
        val streamData: List<TestEvent> = fixture.next()

        every { sseEventListener.getAlbumEventStream() } returnsFluxOf streamData

        val returnResult: FluxExchangeResult<ServerSentEvent<TestEvent>> = webTestClient.get()
            .uri("/events")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        val resultFlux = returnResult.responseBody
            .filter { it.event() != "ping" }
            .map { it.data()!! }

        StepVerifier.create(resultFlux)
            .expectNextSequence(streamData)
            .thenCancel()
            .verify()
    }

    internal data class TestEvent(val value: String) : AlbumEvent
}
