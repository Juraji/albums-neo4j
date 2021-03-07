package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.duplicates.Duplicate
import nl.juraji.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

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
    internal fun `should scan picture duplicates`() {
        val pictureId = fixture.nextString()
        val duplicates = fixture.nextListOf<Duplicate>()

        every { duplicatesService.scanDuplicatesForPicture(any()) } returnsFluxOf duplicates

        webTestClient.post()
            .uri("/pictures/$pictureId/duplicates/scan")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Duplicate>()
            .contains(*duplicates.toTypedArray())
    }
}
