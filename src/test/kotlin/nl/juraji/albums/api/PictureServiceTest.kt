package nl.juraji.albums.api

import com.marcellogalhardo.fixture.next
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.model.Directory
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.Tag
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.repositories.PictureRepository
import nl.juraji.albums.services.FileOperations
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.verifyError
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class PictureServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var fileOperations: FileOperations

    @InjectMockKs
    private lateinit var pictureService: PictureService

    @Test
    internal fun `should get all pictures`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.findAll() } returnsFluxOf picture

        StepVerifier.create(pictureService.getAllPictures())
            .expectNext(picture)
            .verifyComplete()
    }

    @Test
    internal fun `should get picture by id`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture

        StepVerifier.create(pictureService.getPicture(picture.id!!))
            .expectNext(picture)
            .verifyComplete()
    }

    @Test
    internal fun `should add picture`() {
        val expected = fixture.next<Picture>()
        val newPicture = expected.copy(id = null)
        val savedDirectory = slot<Directory>()

        every { fileOperations.exists(any()) } returnsMonoOf true
        every { fileOperations.getParentPathStr(any()) } returns "mock_path"
        every { directoryRepository.findByLocation(any()) }.returnsEmptyMono()
        every { directoryRepository.save(capture(savedDirectory)) }.returnsArgumentAsMono()
        every { pictureRepository.existsByLocation(newPicture.location) } returnsMonoOf false
        every { pictureRepository.save(newPicture) } returnsMonoOf expected

        StepVerifier.create(pictureService.addPicture(newPicture))
            .expectNext(expected)
            .verifyComplete()

        // Verify side-effect of adding to directory
        verify {
            fileOperations.getParentPathStr(newPicture.location)
            directoryRepository.findByLocation("mock_path")
            directoryRepository.save(any())
        }

        assertEquals(expected, savedDirectory.captured.pictures[0].picture)
    }

    @Test
    internal fun `should not add picture when file not exists`() {
        val newPicture = fixture.next<Picture>().copy(id = null)

        every { fileOperations.exists(any()) } returnsMonoOf false
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf false

        StepVerifier.create(pictureService.addPicture(newPicture))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should not add picture when location already in db`() {
        val newPicture = fixture.next<Picture>().copy(id = null)

        every { fileOperations.exists(any()) } returnsMonoOf true
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf true

        StepVerifier.create(pictureService.addPicture(newPicture))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should add tag to picture`() {
        val tag = fixture.next<Tag>()
        val picture = fixture.next<Picture>()
        val savedPicture = slot<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture
        every { pictureRepository.save(capture(savedPicture)) } returnsMonoOf picture

        StepVerifier.create(pictureService.tagPictureBy(picture.id!!, tag))
            .expectNext(picture)
            .verifyComplete()

        assertEquals(tag, savedPicture.captured.tags[0].tag)
    }
}
