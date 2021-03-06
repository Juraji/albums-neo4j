package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsManyMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import java.util.concurrent.ThreadLocalRandom

@WebFluxTest(FolderPicturesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class FolderPicturesControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var picturesService: PicturesService

    @Test
    fun getFolderPictures() {
        val folderId = fixture.nextString()
        val pictures = fixture.nextListOf<Picture>()

        every { picturesService.getFolderPictures(any()) } returnsFluxOf pictures

        webTestClient
            .get()
            .uri("/folders/$folderId/pictures")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Picture>()
            .isEqualTo<WebTestClient.ListBodySpec<Picture>>(pictures)

        verify { picturesService.getFolderPictures(folderId) }
    }

    @Test
    fun `should upload picture`() {
        val folderId = fixture.nextString()
        val expected = fixture.nextListOf<Picture>(3)

        val body = MultipartBodyBuilder().apply {
            part("files[]", object : ByteArrayResource(generateData()) {
                override fun getFilename(): String {
                    return "name"
                }
            })
            part("files[]", object : ByteArrayResource(generateData()) {
                override fun getFilename(): String {
                    return "name"
                }
            })
            part("files[]", object : ByteArrayResource(generateData()) {
                override fun getFilename(): String {
                    return "name"
                }
            })
        }.build()

        every { picturesService.persistNewPicture(any(), any()) } returnsManyMonoOf expected

        webTestClient
            .post()
            .uri("/folders/$folderId/pictures")
            .body(BodyInserters.fromMultipartData(body))
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Picture>()
            .isEqualTo<WebTestClient.ListBodySpec<Picture>>(expected)

        verify(exactly = 3) { picturesService.persistNewPicture(folderId, any()) }
    }

    private fun generateData(): ByteArray {
        val random = ThreadLocalRandom.current()
        val data = ByteArray(1024)
        random.nextBytes(data)
        return data
    }
}
