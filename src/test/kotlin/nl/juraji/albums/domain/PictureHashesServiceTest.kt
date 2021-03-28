package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.sksamuel.scrimage.ImmutableImage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.domain.events.PictureHashGeneratedEvent
import nl.juraji.albums.domain.pictures.PictureHash
import nl.juraji.albums.domain.pictures.PictureHashesRepository
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.ByteArrayResource
import reactor.test.StepVerifier
import java.util.*

@ExtendWith(MockKExtension::class)
internal class PictureHashesServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureHashesRepository: PictureHashesRepository

    @MockK
    private lateinit var picturesService: PicturesService

    @MockK
    private lateinit var imageService: ImageService

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var pictureHashesService: PictureHashesService

    @Test
    fun `should update picture hash`() {
        val pictureId = fixture.nextString()
        val pictureResource = ByteArrayResource(byteArrayOf())
        val image = ImmutableImage.create(10, 10)
        val hashData = BitSet()
        val pictureHash = fixture.next<PictureHash>()

        val expected = pictureHash.copy(data = hashData)

        every { picturesService.getPictureResource(any()) } returnsMonoOf pictureResource
        every { imageService.loadResourceAsImage(any()) } returnsMonoOf image
        every { imageService.generateHash(any()) } returnsMonoOf hashData
        every { pictureHashesRepository.findByPictureId(any()) } returnsMonoOf pictureHash
        every { pictureHashesRepository.save(any()) }.returnsArgumentAsMono()
        justRun { applicationEventPublisher.publishEvent(any<AlbumEvent>()) }

        StepVerifier.create(pictureHashesService.updatePictureHash(pictureId))
            .expectNext(expected)
            .verifyComplete()

        verify {
            picturesService.getPictureResource(pictureId)
            imageService.loadResourceAsImage(pictureResource)
            imageService.generateHash(image)
            pictureHashesRepository.findByPictureId(pictureId)
            pictureHashesRepository.save(expected)
            applicationEventPublisher.publishEvent(PictureHashGeneratedEvent(pictureId))
        }
    }
}
