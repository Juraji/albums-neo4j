package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.api.dto.NewDirectoryDto
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryProps
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


@WebFluxTest(DirectoriesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DirectoriesControllerTest {
    @MockkBean
    private lateinit var directoryService: DirectoryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    internal fun `should get root directories`() {
        val directories: List<Directory> = fixture.next()

        every { directoryService.getRootDirectories() } returnsFluxOf directories

        webTestClient
            .get()
            .uri("/directories/roots")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Directory>()
            .contains(*directories.toTypedArray())
    }

    @Test
    internal fun `should create directory`() {
        val directoryDto = fixture.next<NewDirectoryDto>()
        val directory = fixture.next<Directory>()

        every { directoryService.createDirectory(directoryDto.location) } returnsMonoOf directory

        webTestClient
            .post()
            .uri("/directories")
            .bodyValue(directoryDto)
            .exchange()
            .expectStatus().isOk
            .expectBody<Directory>()
            .isEqualTo(directory)
    }

    @Test
    internal fun `should delete directory`() {
        val directoryId = fixture.nextString()

        every { directoryService.deleteDirectory(directoryId) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri("/directories/$directoryId")
            .exchange()
            .expectStatus().isOk

        verify { directoryService.deleteDirectory(directoryId) }
    }

    @Test
    internal fun `should validate NewDirectoryDto on post`() {
        val directoryDto = NewDirectoryDto(location = "|some invalid path?")

        webTestClient
            .post()
            .uri("/directories")
            .bodyValue(directoryDto)
            .exchange()
            .expectStatus().isBadRequest
    }
}
