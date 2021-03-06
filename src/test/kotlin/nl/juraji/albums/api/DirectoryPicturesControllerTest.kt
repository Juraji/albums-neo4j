package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.api.dto.NewPictureDto
import nl.juraji.albums.api.dto.PictureProps
import nl.juraji.albums.api.dto.toPictureProps
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Mono

@WebFluxTest(DirectoryPicturesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DirectoryPicturesControllerTest {
    @MockkBean
    private lateinit var pictureService: PictureService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    fun `should get pictures by directory id`() {
        val directoryId = fixture.nextString()
        val pictures: List<Picture> = fixture.next()
        val expected = pictures.map(Picture::toPictureProps)

        every { pictureService.getByDirectoryId(any()) } returnsFluxOf pictures

        webTestClient
            .get()
            .uri("/directories/$directoryId/pictures")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<PictureProps>()
            .contains(*expected.toTypedArray())

        verify {
            pictureService.getByDirectoryId(directoryId)
        }
    }

    @Test
    internal fun `should add picture`() {
        val directoryId = fixture.nextString()
        val postBody = fixture.next<NewPictureDto>()
        val picture = fixture.next<Picture>()


        every { pictureService.addPicture(any<String>(), any(), any()) } returnsMonoOf picture

        webTestClient
            .post()
            .uri("/directories/$directoryId/pictures")
            .body(Mono.just(postBody), Picture::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody<Picture>()
            .isEqualTo(picture)

        verify { pictureService.addPicture(directoryId, postBody.location, postBody.name) }
    }

    @Test
    internal fun `should validate NewPictureDto on post`() {
        val pictureDto = NewPictureDto(
            location = "|some invalid path?",
            name = ""
        )

        webTestClient
            .post()
            .uri("/directories/some_id/pictures")
            .bodyValue(pictureDto)
            .exchange()
            .expectStatus().isBadRequest
    }
}
