package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureDeletedEvent
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class PictureDeletedEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var fileOperations: FileOperations

    @InjectMockKs
    private lateinit var pictureDeletedEventListener: PictureDeletedEventListener

    private val picturePath = Paths.get("/some/location/picture.jpg")
    private val picture = fixture.next<Picture>().copy(location = picturePath.toString())

    @Test
    fun `should delete picture image file when doDeleteFile == true`() {
        val event = PictureDeletedEvent(this, picture, doDeleteFile = true)

        every { fileOperations.deleteIfExists(picturePath) } returnsMonoOf true

        pictureDeletedEventListener.deletePictureImageFile(event)

        verify { fileOperations.deleteIfExists(picturePath) }
    }
}
