package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import nl.juraji.albums.domain.pictures.PictureDeletedEvent
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.domain.tags.TagRepository
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import reactor.kotlin.test.verifyError
import reactor.test.StepVerifier
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class PictureServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

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

        verify { pictureRepository.findAll() }
    }

    @Test
    internal fun `should get picture by id`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture

        StepVerifier.create(pictureService.getPicture(picture.id!!))
            .expectNext(picture)
            .verifyComplete()

        verify { pictureRepository.findById(picture.id!!) }
    }

    @Test
    internal fun `should add picture`() {
        val location = Paths.get("/some/location/picture.jpg").toString()
        val name = "picture.jpg"

        val directory = fixture.next<Directory>()
        val savedPicture = fixture.next<Picture>().copy(directory = directory)

        every { fileOperations.exists(any()) } returnsMonoOf true
        every { pictureRepository.existsByLocation(location) } returnsMonoOf false
        every { pictureRepository.save(any()) } returnsMonoOf savedPicture
        every { directoryRepository.existsByLocation(any()) } returnsMonoOf true
        every { directoryRepository.findByLocation(any()) } returnsMonoOf directory
        every { applicationEventPublisher.publishEvent(any<PictureCreatedEvent>()) } just runs

        StepVerifier.create(pictureService.addPicture(location, name))
            .expectNextCount(1)
            .verifyComplete()

        verify {
            fileOperations.exists(any())
            pictureRepository.existsByLocation(location)
            applicationEventPublisher.publishEvent(match<PictureCreatedEvent> {
                it.pictureId == savedPicture.id && it.location == savedPicture.location
            })
        }
    }

    @Test
    internal fun `should not add picture when file not exists`() {
        every { fileOperations.exists(any()) } returnsMonoOf false
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf false
        every { directoryRepository.existsByLocation(any()) } returnsMonoOf true
        every { directoryRepository.findByLocation(any()) }.returnsEmptyMono()

        StepVerifier.create(pictureService.addPicture("/some/location", "name"))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should not add picture when location already in db`() {
        every { fileOperations.exists(any()) } returnsMonoOf true
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf true
        every { directoryRepository.existsByLocation(any()) } returnsMonoOf true
        every { directoryRepository.findByLocation(any()) }.returnsEmptyMono()

        StepVerifier.create(pictureService.addPicture("/some/location", "name"))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should delete picture`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture
        every { pictureRepository.deleteTreeById(picture.id!!) }.returnsEmptyMono()
        every { applicationEventPublisher.publishEvent(any<PictureDeletedEvent>()) } just runs

        StepVerifier.create(pictureService.deletePicture(picture.id!!, true))
            .verifyComplete()

        verify {
            pictureRepository.deleteTreeById(picture.id!!)
            applicationEventPublisher.publishEvent(any<PictureDeletedEvent>())
        }
    }

    @Test
    internal fun `should add tag to picture`() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureRepository.addTag(any(), any()) }.returnsEmptyMono()

        StepVerifier.create(pictureService.addTag(pictureId, tagId))
            .verifyComplete()

        verify { pictureRepository.addTag(pictureId, tagId) }
    }

    @Test
    internal fun `should remove tag from picture`() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureRepository.removeTag(any(), any()) }.returnsEmptyMono()

        StepVerifier.create(pictureService.removeTag(pictureId, tagId))
            .verifyComplete()

        verify { pictureRepository.removeTag(pictureId, tagId) }
    }
}
