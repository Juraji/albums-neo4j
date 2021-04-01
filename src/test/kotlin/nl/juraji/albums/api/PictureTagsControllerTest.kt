package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.PictureTagsService
import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest(PictureTagsController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class PictureTagsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var pictureTagsService: PictureTagsService

    @Test
    fun getPictureTags() {
        val pictureId = fixture.nextString()
        val tags = fixture.nextListOf<Tag>()

        every { pictureTagsService.getPictureTags(any()) } returnsFluxOf tags

        webTestClient
            .get()
            .uri("/pictures/$pictureId/tags")
            .exchange()
            .expectBodyList<Tag>()
            .isEqualTo<ListBodySpec<Tag>>(tags)

        verify { pictureTagsService.getPictureTags(pictureId) }
    }

    @Test
    fun addTagToPicture() {
        val pictureId = fixture.nextString()
        val tag = fixture.next<Tag>()

        every { pictureTagsService.addTagToPicture(any(), any()) } returnsMonoOf tag

        webTestClient
            .post()
            .uri("/pictures/$pictureId/tags/${tag.id}")
            .exchange()
            .expectBody<Tag>()
            .isEqualTo(tag)

        verify { pictureTagsService.addTagToPicture(pictureId, tag.id!!) }
    }

    @Test
    fun removeTagFromPicture() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureTagsService.removeTagFromPicture(any(), any()) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri("/pictures/$pictureId/tags/$tagId")
            .exchange()
            .expectStatus().isOk

        verify { pictureTagsService.removeTagFromPicture(pictureId, tagId) }
    }
}
