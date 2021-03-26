package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PicturesRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.FileSystemResource
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class PicturesServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var picturesRepository: PicturesRepository

    @MockK
    private lateinit var imageService: ImageService

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var picturesService: PicturesService

    @Test
    fun getFolderPictures() {
        val folderId = fixture.nextString()
        val pictures = fixture.nextListOf<Picture>()

        every { picturesRepository.findAllByFolderId(any()) } returnsFluxOf pictures

        val result = picturesService.getFolderPictures(folderId)

        StepVerifier.create(result)
            .expectNextSequence(pictures)
            .verifyComplete()

        verify { picturesRepository.findAllByFolderId(folderId) }
    }

    @Test
    @Disabled
    fun `should persist new picture`() {
        TODO("Figure out how to test")
    }

    @Test
    fun `should get picture resource`() {
        val pictureId = fixture.nextString()
        val picture = fixture.next<Picture>()

        every { picturesRepository.findById(any<String>()) } returnsMonoOf picture

        val result = picturesService.getPictureResource(pictureId)

        StepVerifier.create(result)
            .expectNext(FileSystemResource(picture.pictureLocation))
            .verifyComplete()

        verify { picturesRepository.findById(pictureId) }
    }

    @Test
    fun `should get thumbnail resource`() {
        val pictureId = fixture.nextString()
        val picture = fixture.next<Picture>()

        every { picturesRepository.findById(any<String>()) } returnsMonoOf picture

        val result = picturesService.getThumbnailResource(pictureId)

        StepVerifier.create(result)
            .expectNext(FileSystemResource(picture.thumbnailLocation))
            .verifyComplete()

        verify { picturesRepository.findById(pictureId) }
    }

}
