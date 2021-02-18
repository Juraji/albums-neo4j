package nl.juraji.albums.eventListeners

import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.PictureMetaDataService
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher

@SpringBootTest(classes = [PictureMetaDataEventListener::class])
internal class PictureMetaDataEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @MockkBean
    private lateinit var pictureMetaDataService: PictureMetaDataService

    @Test
    fun `should update picture meta data`() {
        val location = "/some/location"
        val event = fixture.next<PictureCreatedEvent>().copy(location = location)

        every { pictureMetaDataService.updateMetaData(any()) } returnsMonoOf fixture.next()
        every { pictureMetaDataService.updatePictureHash(any()) } returnsMonoOf fixture.next()

        publisher.publishEvent(event)

        verify {
            pictureMetaDataService.updateMetaData(event.pictureId)
            pictureMetaDataService.updatePictureHash(event.pictureId)
        }
    }
}
