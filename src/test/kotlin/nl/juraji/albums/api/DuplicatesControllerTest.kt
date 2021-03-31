package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.pictures.DuplicatesView
import nl.juraji.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest(DuplicatesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DuplicatesControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var duplicatesService: DuplicatesService

    @Test
    fun getAllDuplicates() {
        val expected = fixture.nextListOf<DuplicatesView>()
            .map {
                it.copy(
                    source = it.source.copy(thumbnailLocation = "", pictureLocation = ""),
                    target = it.target.copy(thumbnailLocation = "", pictureLocation = "")
                )
            }

        every { duplicatesService.getAll() } returnsFluxOf expected

        webTestClient
            .get()
            .uri("/duplicates")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DuplicatesView>()
            .isEqualTo<ListBodySpec<DuplicatesView>>(expected)
    }
}
