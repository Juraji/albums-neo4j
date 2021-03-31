package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.TagsService
import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.returnsEmptyMono
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

@WebFluxTest(TagsController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class TagsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var tagsService: TagsService

    @Test
    fun `should get all tags`() {
        val tags = fixture.nextListOf<Tag>()

        every { tagsService.getAllTags() } returnsFluxOf tags

        webTestClient
            .get()
            .uri("/tags")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Tag>()
            .contains(*tags.toTypedArray())

        verify { tagsService.getAllTags() }
    }

    @Test
    fun `should create tag`() {
        val tag = fixture.next<Tag>().copy(
                color = "#000000",
                textColor = "#ffffff"
            )

        every { tagsService.createTag(any()) } returnsMonoOf tag

        webTestClient
            .post()
            .uri("/tags")
            .bodyValue(tag)
            .exchange()
            .expectStatus().isOk
            .expectBody<Tag>()
            .isEqualTo(tag)

        verify { tagsService.createTag(tag) }
    }

    @Test
    fun `should update tag`() {
        val update = fixture.next<Tag>().copy(
            color = "#000000",
            textColor = "#ffffff"
        )

        every { tagsService.updateTag(any(), any()) } returnsMonoOf update

        webTestClient
            .put()
            .uri("/tags/${update.id}")
            .bodyValue(update)
            .exchange()
            .expectStatus().isOk
            .expectBody<Tag>()
            .isEqualTo(update)

        verify { tagsService.updateTag(update.id!!, update) }
    }

    @Test
    fun `should delete tag`() {
        val tagId = fixture.nextString()

        every { tagsService.deleteTag(any()) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri("/tags/$tagId")
            .exchange()
            .expectStatus().isOk

        verify { tagsService.deleteTag(tagId) }
    }
}
