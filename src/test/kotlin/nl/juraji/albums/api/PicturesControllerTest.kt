package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.PicturesService
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsManyMonoOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
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

    @Test
    fun getFolderPictures() {
        val folderId = fixture.nextString()
        val pictures = fixture.nextListOf<Picture>()
        val expected = pictures.map {
            it.copy(
                pictureLocation = "",
                thumbnailLocation = "",
            )
        }

        every { picturesService.getFolderPictures(any()) } returnsFluxOf pictures

        webTestClient
            .get()
            .uri("/folders/$folderId/pictures")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Picture>()
            .isEqualTo<ListBodySpec<Picture>>(expected)

        verify { picturesService.getFolderPictures(folderId) }
    }

    @Test
    fun `should upload picture`() {
        val folderId = fixture.nextString()
        val expected = fixture.nextListOf<Picture>(3).map {
            it.copy(
                pictureLocation = "",
                thumbnailLocation = "",
            )
        }

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
            .uri("/folders/$folderId/pictures/upload")
            .body(BodyInserters.fromMultipartData(body))
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Picture>()
            .isEqualTo<ListBodySpec<Picture>>(expected)

        verify(exactly = 3) { picturesService.persistNewPicture(folderId, any()) }
    }

    @Test
    fun `should download picture`() {
        val folderId = fixture.nextString()
        val pictureId = fixture.nextString()
        val data = generateData()

        every { picturesService.getPictureResource(pictureId) } returnsMonoOf ByteArrayResource(data)

        webTestClient
            .get()
            .uri("/folders/$folderId/pictures/$pictureId/download")
            .exchange()
            .expectStatus().isOk
            .expectBody<ByteArray>()
            .isEqualTo(data)

        verify { picturesService.getPictureResource(pictureId) }
    }

    @Test
    fun `should download thumbnail`() {
        val folderId = fixture.nextString()
        val pictureId = fixture.nextString()
        val data = generateData()

        every { picturesService.getThumbnailResource(pictureId) } returnsMonoOf ByteArrayResource(data)

        webTestClient
            .get()
            .uri("/folders/$folderId/pictures/$pictureId/thumbnail")
            .exchange()
            .expectStatus().isOk
            .expectBody<ByteArray>()
            .isEqualTo(data)

        verify { picturesService.getThumbnailResource(pictureId) }
    }

    private fun generateData(): ByteArray {
        val random = ThreadLocalRandom.current()
        val data = ByteArray(1024)
        random.nextBytes(data)
        return data
    }
}
