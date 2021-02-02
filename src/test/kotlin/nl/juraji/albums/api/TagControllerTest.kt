package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.model.Tag
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
import reactor.core.publisher.Mono

@WebFluxTest(TagController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class TagControllerTest {

    @MockkBean
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    internal fun `should fetch all tags`() {
        val expected: List<Tag> = listOf(fixture.next(), fixture.next(), fixture.next(), fixture.next())

        every { tagService.getAllTags() } returnsFluxOf expected

        webTestClient
            .get()
            .uri("/tags")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Tag::class.java)
            .contains(*expected.toTypedArray())
    }

    @Test
    internal fun `should create new tag`() {
        val expected = fixture.next<Tag>()
        val postedTag = expected.copy(id = null)

        every { tagService.createTag(postedTag) } returnsMonoOf expected

        webTestClient
            .post()
            .uri("/tags")
            .body(Mono.just(postedTag), Tag::class.java)
            .exchange()
            .expectBody<Tag>()
            .isEqualTo(expected)
    }

    @Test
    internal fun `should delete tag`() {
        val tagId = fixture.nextString()

        every { tagService.deleteTag(tagId) } returnsMonoOf Unit

        webTestClient
            .delete()
            .uri("/tags/$tagId")
            .exchange()
            .expectStatus().isOk
    }
}
