package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.concurrent.ThreadLocalRandom

@WebFluxTest(PicturesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class PicturesControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var picturesService: PicturesService

    @MockkBean
    private lateinit var duplicatesService: DuplicatesService

    @Test
    fun `should download picture`() {
        val pictureId = fixture.nextString()
        val data = generateData()

        every { picturesService.getPictureResource(pictureId) } returnsMonoOf ByteArrayResource(data)

        webTestClient
            .get()
            .uri("/pictures/$pictureId/download")
            .exchange()
            .expectStatus().isOk
            .expectBody<ByteArray>()
            .isEqualTo(data)

        verify { picturesService.getPictureResource(pictureId) }
    }

    @Test
    fun `should download thumbnail`() {
        val pictureId = fixture.nextString()
        val data = generateData()

        every { picturesService.getThumbnailResource(pictureId) } returnsMonoOf ByteArrayResource(data)

        webTestClient
            .get()
            .uri("/pictures/$pictureId/thumbnail")
            .exchange()
            .expectStatus().isOk
            .expectBody<ByteArray>()
            .isEqualTo(data)

        verify { picturesService.getThumbnailResource(pictureId) }
    }

    @Test
    fun deleteDuplicateFromPicture() {
        val pictureId = fixture.nextString()
        val targetId = fixture.nextString()

        every { duplicatesService.removeDuplicate(any(), any()) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri("/pictures/$pictureId/duplicates/$targetId")
            .exchange()
            .expectStatus().isOk

        verify { duplicatesService.removeDuplicate(pictureId, targetId) }
    }

    private fun generateData(): ByteArray {
        val random = ThreadLocalRandom.current()
        val data = ByteArray(1024)
        random.nextBytes(data)
        return data
    }
}
