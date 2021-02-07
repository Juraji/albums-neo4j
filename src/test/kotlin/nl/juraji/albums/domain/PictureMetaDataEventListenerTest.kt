package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.returnsVoidMono
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.awt.image.BufferedImage
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

@ExtendWith(MockKExtension::class)
internal class PictureMetaDataEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var fileOperations: FileOperations

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @InjectMockKs
    private lateinit var pictureMetaDataEventListener: PictureMetaDataEventListener

    private val directoryPath = Paths.get("/some/location")
    private val directory = fixture.next<Directory>().copy(location = directoryPath.toString())
    private val picturePath = Paths.get("/some/location/picture.jpg")
    private val picture = fixture.next<Picture>().copy(location = picturePath.toString())
    private val event = PictureCreatedEvent(this, picture)

    @Test
    internal fun `should add picture to directory`() {
        every { directoryRepository.findByLocation(directory.location) } returnsMonoOf directory
        every { directoryRepository.addPicture(directory.id!!, picture.id!!) }.returnsVoidMono()

        pictureMetaDataEventListener.addPictureToDirectory(event)

        verify {
            directoryRepository.findByLocation(directory.location)
            directoryRepository.addPicture(directory.id!!, picture.id!!)
        }
    }

    @Test
    internal fun `should update picture meta data`() {
        val fileAttributes: BasicFileAttributes = fixture.next()
        val testPicture = BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB)

        every { fileOperations.readAttributes(picturePath) } returnsMonoOf fileAttributes
        every { fileOperations.readImage(picturePath) } returnsMonoOf testPicture
        every { fileOperations.readContentType(picturePath) } returnsMonoOf "image/jpeg"
        every { pictureRepository.save(any()) }.returnsArgumentAsMono()

        pictureMetaDataEventListener.updatePictureMetaData(event)

        verify {
            fileOperations.readAttributes(picturePath)
            fileOperations.readImage(picturePath)
            fileOperations.readContentType(picturePath)
            pictureRepository.save(any())
        }
    }

    @Test
    internal fun `should generate picture image hash`() {
        pictureMetaDataEventListener.generatePictureImageHash(event)
    }
}
