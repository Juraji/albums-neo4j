package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.api.dto.NewPictureDto
import nl.juraji.albums.api.dto.PictureDto
import nl.juraji.albums.api.dto.TagDto
import nl.juraji.albums.api.dto.toPictureDto
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.PictureDescription
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.uriBuilder
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
        val picture = fixture.next<PictureDescription>()
        val expected = picture.toPictureDto()

        every { pictureService.getAllPictures() } returnsFluxOf picture

        webTestClient
            .get()
            .uri("/pictures")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<PictureDto>()
            .contains(expected)
    }

    @Test
    internal fun `should get picture by id`() {
        val picture = fixture.next<PictureDescription>()
        val expected = picture.toPictureDto()

        every { pictureService.getPicture(picture.id) } returnsMonoOf picture

        webTestClient
            .get()
            .uri("/pictures/${picture.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody<PictureDto>()
            .isEqualTo(expected)
    }

    @Test
    internal fun `should add picture`() {
        val postBody = fixture.next<NewPictureDto>()
        val picture = fixture.next<Picture>()
        val expected = picture.toPictureDto()


        every { pictureService.addPicture(postBody.location, postBody.name) } returnsMonoOf picture

        webTestClient
            .post()
            .uri("/pictures")
            .body(Mono.just(postBody), Picture::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody<PictureDto>()
            .isEqualTo(expected)
    }

    @Test
    internal fun `should delete picture`() {
        val pictureId = fixture.nextString()

        every { pictureService.deletePicture(pictureId, false) } returnsMonoOf Unit

        webTestClient
            .delete()
            .uriBuilder {
                path("/pictures/$pictureId")
                queryParam("deleteFile", false)
            }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    internal fun `should add tag to picture`() {
        val picture = fixture.next<Picture>()
        val postedTag = fixture.next<TagDto>()
        val expected = picture.toPictureDto()

        every { pictureService.tagPictureBy(picture.id!!, postedTag.id) } returnsMonoOf picture

        webTestClient
            .post()
            .uri("/pictures/${picture.id}/tags")
            .body(Mono.just(postedTag), TagDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody<PictureDto>()
            .isEqualTo(expected)

        verify { pictureService.tagPictureBy(picture.id!!, postedTag.id) }
    }

    @Test
    internal fun `should remove tag from picture`() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureService.removeTagFromPicture(pictureId, tagId) } returnsMonoOf Unit

        webTestClient
            .delete()
            .uri("/pictures/$pictureId/tags/$tagId")
            .exchange()
            .expectStatus().isOk
    }
}
