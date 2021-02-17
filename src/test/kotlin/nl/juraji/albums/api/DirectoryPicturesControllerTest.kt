package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.PictureService
import nl.juraji.albums.domain.pictures.PictureProps
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest(DirectoryPicturesController::class)
@AutoConfigureWebTestClient
@Import(TestFixtureConfiguration::class)
internal class DirectoryPicturesControllerTest {
    @MockkBean
    private lateinit var pictureService: PictureService

    @MockkBean
    private lateinit var directoryService: DirectoryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var fixture: Fixture

    @Test
    fun `should get pictures by directory id`() {
        val directoryId = fixture.nextString()
        val pictures: List<PictureProps> = fixture.next()

        every { pictureService.getByDirectoryId(any(), any()) } returnsFluxOf pictures

        webTestClient
            .get()
            .uri{ uriBuilder ->
                uriBuilder
                    .path("/directories/$directoryId/pictures")
                    .queryParam("page", "1")
                    .queryParam("size", "50")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<PictureProps>()
            .contains(*pictures.toTypedArray())

        verify {
            pictureService.getByDirectoryId(directoryId, PageRequest.of(1, 50))
        }
    }

    @Test
    internal fun `should update directory pictures`() {
        val directoryId = fixture.nextString()

        every { directoryService.updatePicturesFromDisk(any()) }.returnsEmptyMono()

        webTestClient.post()
            .uri("/directories/$directoryId/update")
            .exchange()
            .expectStatus().isOk


        verify {
            directoryService.updatePicturesFromDisk(directoryId)
        }
    }
}
