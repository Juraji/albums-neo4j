package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import com.sksamuel.scrimage.ImmutableImage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.events.AlbumEvent
import nl.juraji.albums.domain.events.PictureAddedEvent
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PicturesRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.hamcrest.Matchers.samePropertyValuesAs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Path

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
    fun `should persist new picture`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_TYPE to MediaType.IMAGE_JPEG_VALUE,
            HttpHeaders.CONTENT_DISPOSITION to ContentDisposition.formData().filename("image.jpg").build().toString()
        )

        val testImage = ImmutableImage.create(100, 150)

        val expectedToSave = Picture(
            name = "image.jpg",
            fileSize = 64000,
            width = 100,
            height = 150,
            type = FileType.JPEG,
            pictureLocation = "saved-image.jpg",
            thumbnailLocation = "saved-thumbnail.jpg",
        )
        val expectedAfterSave = expectedToSave.copy(
            id = fixture.nextString()
        )

        every { imageService.loadPartAsImage(any()) } returnsMonoOf testImage
        every { imageService.savePicture(any(), any()) } returnsMonoOf ImageService.SavedPicture(
            "saved-image.jpg",
            64000
        )
        every { imageService.saveThumbnail(any()) } returnsMonoOf ImageService.SavedPicture(
            "saved-thumbnail.jpg",
            0
        )
        every { picturesRepository.existsByNameInFolder(any(), any()) } returnsMonoOf false
        every { picturesRepository.save(any()) } returnsMonoOf expectedAfterSave
        every { picturesRepository.addPictureToFolder(any(), any()) } returnsMonoOf expectedAfterSave
        justRun { applicationEventPublisher.publishEvent(any<AlbumEvent>()) }

        val result = picturesService.persistNewPicture(folderId, filePart)

        StepVerifier.create(result)
            .expectNext(expectedAfterSave)
            .verifyComplete()

        verify {
            imageService.loadPartAsImage(filePart)
            imageService.savePicture(testImage, FileType.JPEG)
            imageService.saveThumbnail(testImage)
            picturesRepository.existsByNameInFolder(folderId, "image.jpg")
            picturesRepository.save(match {
                samePropertyValuesAs(expectedToSave, "addedOn").matches(it)
            })
            picturesRepository.addPictureToFolder(folderId, expectedAfterSave.id!!)
            applicationEventPublisher.publishEvent(PictureAddedEvent(folderId, expectedAfterSave))
        }
    }

    @Test
    fun `should fail persist new picture if content-type header is missing`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_DISPOSITION to ContentDisposition.formData().filename("image.jpg").build().toString()
        )

        val result = picturesService.persistNewPicture(folderId, filePart)

        StepVerifier.create(result)
            .expectErrorMessage("Missing Content-Type header")
            .verify()
    }

    @Test
    fun `should fail persist new picture if content-type is not supported`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_TYPE to MediaType.APPLICATION_PDF_VALUE,
            HttpHeaders.CONTENT_DISPOSITION to ContentDisposition.formData().filename("image.jpg").build().toString()
        )

        val result = picturesService.persistNewPicture(folderId, filePart)

        StepVerifier.create(result)
            .expectErrorMessage("Unsupported Content-Type: application/pdf")
            .verify()
    }

    @Test
    fun `should fail persist new picture if content-disposition header is missing`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_TYPE to MediaType.IMAGE_JPEG_VALUE
        )

        val result = picturesService.persistNewPicture(folderId, filePart)

        StepVerifier.create(result)
            .expectErrorMessage("Missing Content-Disposition header or filename is undefined")
            .verify()
    }

    @Test
    fun `should fail persist new picture if filename is not in content-disposition header`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_TYPE to MediaType.IMAGE_JPEG_VALUE,
            HttpHeaders.CONTENT_DISPOSITION to ContentDisposition.formData().build().toString()
        )

        val result = picturesService.persistNewPicture(folderId, filePart)

        StepVerifier.create(result)
            .expectErrorMessage("Missing Content-Disposition header or filename is undefined")
            .verify()
    }

    @Test
    fun `should fail persist new picture if same filename already exists in folder`() {
        val folderId = fixture.nextString()
        val filePart = filePartOf(
            HttpHeaders.CONTENT_TYPE to MediaType.IMAGE_JPEG_VALUE,
            HttpHeaders.CONTENT_DISPOSITION to ContentDisposition.formData().filename("image.jpg").build().toString()
        )

        every { picturesRepository.existsByNameInFolder(any(), any()) } returnsMonoOf true

        StepVerifier.create(picturesService.persistNewPicture(folderId, filePart))
            .expectErrorMessage("A file with name image.jpg already exists in folder with id $folderId")
            .verify()

        verify { picturesRepository.existsByNameInFolder(folderId, "image.jpg") }
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

    private fun filePartOf(
        vararg headers: Pair<String, String>
    ): FilePart = object : FilePart {
        override fun name(): String = "files[]"

        override fun headers(): HttpHeaders = HttpHeaders().apply {
            headers.forEach { (header, value) -> set(header, value) }
        }

        override fun content(): Flux<DataBuffer> =
            Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(byteArrayOf()))

        override fun filename(): String = "files[]"

        override fun transferTo(dest: Path): Mono<Void> =
            throw UnsupportedOperationException()
    }
}
