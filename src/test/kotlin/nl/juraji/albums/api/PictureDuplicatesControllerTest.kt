package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(PictureDuplicatesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class PictureDuplicatesControllerTest {

    @MockkBean
    private lateinit var duplicatesService: DuplicatesService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    internal fun `should remove duplicate from picture`() {
        val pictureId = fixture.nextString()
        val targetId = fixture.nextString()

        every { duplicatesService.removeDuplicateFromPicture(pictureId, targetId) } returnsMonoOf Unit

        webTestClient.delete()
            .uri("/pictures/$pictureId/duplicates/$targetId")
            .exchange()
            .expectStatus().isOk
    }
}
