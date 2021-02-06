package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.relationships.DuplicatedByWithSource
import nl.juraji.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest(DuplicatesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DuplicatesControllerTest {
    @MockkBean
    private lateinit var duplicatesService: DuplicatesService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    fun `should get all DUPLICATED_BY relationships (distinct)`() {
        val duplicates: List<DuplicatedByWithSource> = fixture.next()

        every { duplicatesService.findAllDistinctDuplicatedBy() } returnsFluxOf duplicates

        webTestClient.get()
            .uri("/duplicates")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DuplicatedByWithSource>()
            .contains(*duplicates.toTypedArray())
    }
}
