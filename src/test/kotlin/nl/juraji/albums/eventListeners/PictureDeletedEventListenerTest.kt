package nl.juraji.albums.eventListeners

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.FileOperations
import nl.juraji.albums.domain.pictures.PictureDeletedEvent
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.toPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher

@SpringBootTest(classes = [PictureDeletedEventListener::class])
internal class PictureDeletedEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @MockkBean
    private lateinit var fileOperations: FileOperations

    @Test
    fun `should delete picture image file`() {
        val pictureId = fixture.nextString()
        val location = "/some/location"
        val event = PictureDeletedEvent(this, pictureId, location, true)

        every { fileOperations.deleteIfExists(any()) } returnsMonoOf true

        publisher.publishEvent(event)

        verify {
            fileOperations.deleteIfExists(location.toPath())
        }
    }

    @Test
    fun `should not delete picture image file when doDeleteFile == false`() {
        val pictureId = ""
        val location = "/some/location"
        val event = PictureDeletedEvent(this, pictureId, location, false)

        publisher.publishEvent(event)

        verify {
            fileOperations.deleteIfExists(any()) wasNot Called
        }
    }
}
