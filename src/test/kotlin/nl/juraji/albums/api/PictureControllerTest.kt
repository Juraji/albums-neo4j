package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.Tag
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Mono

@WebFluxTest(PictureController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class PictureControllerTest {
    @MockkBean
    private lateinit var pictureService: PictureService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    internal fun `should get all pictures`() {
        val picture = fixture.next<Picture>()

        every { pictureService.getAllPictures() } returnsFluxOf picture

        webTestClient
            .get()
            .uri("/pictures")
            .exchange()
            .expectBodyList<Picture>()
            .contains(picture)
    }

    @Test
    internal fun `should get picture by id`() {
        val picture = fixture.next<Picture>()

        every { pictureService.getPicture(picture.id!!) } returnsMonoOf picture

        webTestClient
            .get()
            .uri("/pictures/${picture.id}")
            .exchange()
            .expectBody<Picture>()
            .isEqualTo(picture)
    }

    @Test
    internal fun `should add picture`() {
        val expected = fixture.next<Picture>()
        val postBody = expected.copy(id = null)

        every { pictureService.addPicture(postBody) } returnsMonoOf expected

        webTestClient
            .post()
            .uri("/pictures")
            .body(Mono.just(postBody), Picture::class.java)
            .exchange()
            .expectBody<Picture>()
            .isEqualTo(expected)
    }

    @Test
    internal fun `should add tag to picture`() {
        val picture = fixture.next<Picture>()
        val tag = fixture.next<Tag>()

        every { pictureService.tagPictureBy(picture.id!!, tag) } returnsMonoOf picture

        webTestClient
            .post()
            .uri("/pictures/${picture.id}/tags")
            .body(Mono.just(tag), Tag::class.java)
            .exchange()
            .expectBody<Picture>()
            .isEqualTo(picture)

        verify { pictureService.tagPictureBy(picture.id!!, tag) }
    }
}
