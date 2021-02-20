package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.painters.RadialGradient
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import nl.juraji.albums.configuration.DuplicateScannerConfiguration
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.awt.Color

@ExtendWith(MockKExtension::class)
internal class PictureMetaDataServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var fileOperations: FileOperations

    @MockK
    private lateinit var hashDataRepository: HashDataRepository

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @SpyK
    private var duplicateScannerConfiguration = DuplicateScannerConfiguration(hashSampleSize = 10)

    @InjectMockKs
    private lateinit var pictureMetaDataService: PictureMetaDataService

    @Test
    fun `should update meta data`() {
        val picture = fixture.next<Picture>()
        val attributes = fixture.next<FileAttributes>()
        val image = ImmutableImage.create(118, 346)

        val savedPicture = slot<Picture>()

        every { pictureRepository.findById(any<String>()) } returnsMonoOf picture
        every { fileOperations.readAttributes(any()) } returnsMonoOf attributes
        every { fileOperations.loadImage(any()) } returnsMonoOf image
        every { fileOperations.readContentType(any()) } returnsMonoOf "image/jpeg"
        every { pictureRepository.save(capture(savedPicture)) }.returnsArgumentAsMono()
        every { applicationEventPublisher.publishEvent(any<AlbumEvent>()) } just runs

        StepVerifier.create(pictureMetaDataService.updateMetaData(picture.id!!))
            .expectNextCount(1)
            .verifyComplete()

        assertTrue(savedPicture.isCaptured)
        assertEquals(118, savedPicture.captured.width)
        assertEquals(346, savedPicture.captured.height)
        assertEquals(attributes.size, savedPicture.captured.fileSize)
        assertEquals(FileType.JPEG, savedPicture.captured.fileType)
        assertEquals(attributes.lastModifiedTime, savedPicture.captured.lastModified)

        verify {
            pictureRepository.findById(picture.id!!)
            fileOperations.readAttributes(picture.location.toPath())
            fileOperations.loadImage(picture.location.toPath())
            fileOperations.readContentType(picture.location.toPath())
            pictureRepository.save(any())
            applicationEventPublisher.publishEvent(PictureUpdatedEvent(picture.id!!))
        }
    }

    @Test
    fun `should update picture hash`() {
        val picture = fixture.next<Picture>()
        val image = ImmutableImage.create(100, 100)
            .fill(RadialGradient(50f, 50f, 100f, floatArrayOf(0f, 1f), arrayOf(Color.BLACK, Color.GREEN)))

        val savedHashData = slot<HashData>()
        val expectedHash = "4IMPPvjggw8++OCDAw=="

        every { pictureRepository.findById(any<String>()) } returnsMonoOf picture
        every { fileOperations.loadImage(any()) } returnsMonoOf image
        every { hashDataRepository.save(capture(savedHashData)) } answers { firstArg<HashData>().copy(id = "hd1").toMono() }
        every { applicationEventPublisher.publishEvent(any<AlbumEvent>()) } just runs

        StepVerifier.create(pictureMetaDataService.updatePictureHash(picture.id!!))
            .verifyComplete()

        verify {
            duplicateScannerConfiguration.hashSampleSize
            pictureRepository.findById(picture.id!!)
            fileOperations.loadImage(picture.location.toPath())
            hashDataRepository.save(any())
            applicationEventPublisher.publishEvent(PictureUpdatedEvent(picture.id!!))
        }

        assertTrue(savedHashData.isCaptured)
        assertEquals(picture, savedHashData.captured.picture)
        assertEquals(expectedHash, savedHashData.captured.hash)
    }
}
