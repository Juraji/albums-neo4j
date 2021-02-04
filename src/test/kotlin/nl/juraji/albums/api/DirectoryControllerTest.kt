package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.albums.api.dto.DirectoryDto
import nl.juraji.albums.api.dto.toDirectoryDto
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.DirectoryDescription
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


@WebFluxTest(DirectoryController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DirectoryControllerTest {
    @MockkBean
    private lateinit var directoryService: DirectoryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    internal fun `get directories`() {
        val directory = fixture.next<DirectoryDescription>()
        val expected = directory.toDirectoryDto()

        every { directoryService.getAllDirectories() } returnsFluxOf directory

        webTestClient
            .get()
            .uri("/directories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DirectoryDto>()
            .contains(expected)
    }

    @Test
    internal fun `get directory by id`() {
        val directory = fixture.next<DirectoryDescription>()
        val expected = directory.toDirectoryDto()

        every { directoryService.getDirectory(expected.id) } returnsMonoOf directory

        webTestClient
            .get()
            .uri("/directories/${expected.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<DirectoryDto>()
            .isEqualTo(expected)
    }
}
