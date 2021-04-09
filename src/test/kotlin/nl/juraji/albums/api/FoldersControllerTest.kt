package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.FoldersService
import nl.juraji.albums.domain.folders.Folder
import nl.juraji.albums.domain.folders.FolderTreeView
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest(FoldersController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class FoldersControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @MockkBean
    private lateinit var foldersService: FoldersService

    @Test
    fun `should get roots`() {
        val folders = fixture.nextListOf<Folder>().map { FolderTreeView(it.id!!, it.name, emptyList(), true) }

        every { foldersService.getTree() } returnsFluxOf folders

        webTestClient
            .get()
            .uri("/folders/roots")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<FolderTreeView>()
            .contains(*folders.toTypedArray())

        verify { foldersService.getTree() }
    }

    @Test
    fun `should create folder`() {
        val newFolder = fixture.next<Folder>().copy(id = null)

        every { foldersService.createFolder(any(), any()) }.returnsArgumentAsMono()

        val response = webTestClient
            .post()
            .uri("/folders")
            .bodyValue(newFolder)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Folder>()
            .returnResult()

        assertEquals(newFolder.name, response.responseBody?.name)

        verify { foldersService.createFolder(newFolder, "") }
    }

    @Test
    fun `should create folder with parent id`() {
        val parentId = fixture.nextString()
        val newFolder = fixture.next<Folder>().copy(id = null)

        every { foldersService.createFolder(any(), any()) }.returnsArgumentAsMono()

        val response = webTestClient
            .post()
            .uri {
                it.path("/folders")
                    .queryParam("parentId", parentId)
                    .build()
            }
            .bodyValue(newFolder)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Folder>()
            .returnResult()

        assertEquals(newFolder.name, response.responseBody?.name)

        verify { foldersService.createFolder(newFolder, parentId) }
    }

    @Test
    fun `should update folder`() {
        val folderId = fixture.nextString()
        val update = fixture.next<Folder>()

        every { foldersService.updateFolder(any(), any()) }.returnsArgumentAsMono(1)

        webTestClient
            .put()
            .uri("/folders/${folderId}")
            .bodyValue(update)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Folder>()
            .isEqualTo(update)

        verify { foldersService.updateFolder(folderId, update) }
    }

    @Test
    fun `should delete folder`() {
        val folderId = fixture.nextString()

        every { foldersService.deleteFolder(any(), any()) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri("/folders/${folderId}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        verify { foldersService.deleteFolder(folderId, false) }
    }

    @Test
    fun `should delete folder recursive`() {
        val folderId = fixture.nextString()

        every { foldersService.deleteFolder(any(), any()) }.returnsEmptyMono()

        webTestClient
            .delete()
            .uri {
                it.path("/folders/${folderId}")
                    .queryParam("recursive", "true")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        verify { foldersService.deleteFolder(folderId, true) }
    }

    @Test
    fun `should move folder`() {
        val folderId = fixture.nextString()
        val targetId = fixture.nextString()
        val expected = fixture.next<Folder>()

        every { foldersService.moveFolder(any(), any()) } returnsMonoOf expected

        webTestClient
            .post()
            .uri("/folders/$folderId/move-to/$targetId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Folder>()
            .isEqualTo(expected)
    }
}
