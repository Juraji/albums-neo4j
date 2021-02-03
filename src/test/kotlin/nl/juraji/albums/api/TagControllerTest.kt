package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.api.dto.TagDto
import nl.juraji.albums.api.dto.toTagDto
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
        val tag: Tag = fixture.next()
        val expected = tag.toTagDto()

        every { tagService.getAllTags() } returnsFluxOf tag

        webTestClient
            .get()
            .uri("/tags")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(TagDto::class.java)
            .contains(expected)
    }

    @Test
    internal fun `should create new tag`() {
        val postedTag = fixture.next<NewTagDto>()
        val tag = fixture.next<Tag>()
        val expected = tag.toTagDto()

        every { tagService.createTag(postedTag.label, postedTag.color) } returnsMonoOf tag

        webTestClient
            .post()
            .uri("/tags")
            .body(Mono.just(postedTag), NewTagDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody<TagDto>()
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
