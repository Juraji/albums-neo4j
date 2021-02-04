package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.TagService
import nl.juraji.albums.domain.tags.Tag
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
        val tag: Tag = fixture.next()

        every { tagService.getAllTags() } returnsFluxOf tag

        webTestClient
            .get()
            .uri("/tags")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Tag>()
            .contains(tag)
    }

    @Test
    internal fun `should create new tag`() {
        val postedTag = fixture.next<NewTagDto>()
        val tag = fixture.next<Tag>()

        every { tagService.createTag(postedTag.label, postedTag.color) } returnsMonoOf tag

        webTestClient
            .post()
            .uri("/tags")
            .body(Mono.just(postedTag), NewTagDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody<Tag>()
            .isEqualTo(tag)
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
