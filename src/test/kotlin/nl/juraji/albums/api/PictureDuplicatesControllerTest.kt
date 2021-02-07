package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.relationships.DuplicatedBy
import nl.juraji.albums.util.returnsEmptyMono
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
    internal fun `should get duplicated by picture`() {
        val pictureId = fixture.nextString()
        val duplicates: List<DuplicatedBy> = fixture.next()

        every { duplicatesService.findDuplicatedByByPictureId(pictureId) } returnsFluxOf duplicates

        webTestClient.get()
            .uri("/pictures/$pictureId/duplicates")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DuplicatedBy>()
            .contains(*duplicates.toTypedArray())
    }

    @Test
    internal fun `should remove duplicate from picture`() {
        val pictureId = fixture.nextString()
        val targetId = fixture.nextString()

        every { duplicatesService.unsetDuplicatePicture(pictureId, targetId) }.returnsEmptyMono()

        webTestClient.delete()
            .uri("/pictures/$pictureId/duplicates/$targetId")
            .exchange()
            .expectStatus().isOk
    }
}