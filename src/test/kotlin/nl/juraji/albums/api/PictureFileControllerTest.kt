package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(PictureFileController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class PictureFileControllerTest {
    @MockkBean
    private lateinit var pictureService: PictureService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    fun getPictureImage() {
        val pictureId = fixture.nextString()
        val location = "src/test/resources/test-image.jpg"

        every { pictureService.getImageLocationById(any()) } returnsMonoOf location

        webTestClient
            .get()
            .uri("/pictures/${pictureId}/files/image")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.IMAGE_JPEG)
            .expectHeader().contentLength(1180)
            .expectBody<Resource>()
            .consumeWith { assertTrue(it.responseBody is ByteArrayResource) }
    }
}
