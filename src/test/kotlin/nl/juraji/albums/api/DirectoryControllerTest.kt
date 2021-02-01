package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.model.Directory
import nl.juraji.albums.util.assertEqualsTo
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono


@WebFluxTest(DirectoryController::class)
@AutoConfigureWebTestClient
internal class DirectoryControllerTest {
    private val fixture = Fixture()

    @MockkBean
    private lateinit var directoryService: DirectoryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    internal fun setUp() {
        fixture.register(Directory::class) { Directory(id = it.nextLong(), location = it.nextString()) }
    }

    @Test
    internal fun `get directories`() {
        val expected: List<Directory> = listOf(fixture.next(), fixture.next(), fixture.next(), fixture.next())

        every { directoryService.getDirectories() } returnsFluxOf expected

        webTestClient
            .get()
            .uri("/directories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Directory::class.java)
            .contains(*expected.toTypedArray())
    }

    @Test
    internal fun `get directory by id`() {
        val expected = fixture.next<Directory>().copy(id = 1)

        every { directoryService.getDirectory(1) } returnsMonoOf expected

        webTestClient
            .get()
            .uri("/directories/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Directory::class.java)
            .returnResult()
            .assertEqualsTo(expected)
    }

    @Test
    internal fun `should create directory`() {
        val postBody = fixture.next<Directory>().copy(id = null)
        val expected = fixture.next<Directory>()

        every { directoryService.createDirectory(postBody) } returnsMonoOf expected

        webTestClient
            .post()
            .uri("/directories")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(postBody), Directory::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody(Directory::class.java)
            .returnResult()
            .assertEqualsTo(expected)
    }
}
