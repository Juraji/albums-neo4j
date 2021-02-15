package nl.juraji.albums.eventListeners

import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
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

    @SpykBean
    private lateinit var pictureMetaDataEventListener: PictureMetaDataEventListener

    @Test
    fun `should update picture meta data`() {
        val location = "/some/location"
        val event = fixture.next<PictureCreatedEvent>().copy(location = location)

        // Mock other listeners in component
        every { pictureMetaDataEventListener.generatePictureImageHash(any()) } just runs

        every { pictureMetaDataService.updateMetaData(any()) } returnsMonoOf fixture.next()

        publisher.publishEvent(event)

        verify { pictureMetaDataService.updateMetaData(event.pictureId) }
    }

    @Test
    fun `should generate picture image hash`() {
        val location = "/some/location"
        val event = fixture.next<PictureCreatedEvent>().copy(location = location)

        // Mock other listeners in component
        every { pictureMetaDataEventListener.updatePictureMetaData(any()) } just runs

        every { pictureMetaDataService.updatePictureHash(any()) } returnsMonoOf fixture.next()

        publisher.publishEvent(event)

        verify { pictureMetaDataService.updatePictureHash(event.pictureId) }
    }
}
