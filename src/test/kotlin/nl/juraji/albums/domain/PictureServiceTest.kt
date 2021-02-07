package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureCreatedEvent
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.*
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import reactor.kotlin.test.verifyError
import reactor.test.StepVerifier
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

@ExtendWith(MockKExtension::class)
internal class PictureServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()
        .also {
            it.register(BasicFileAttributes::class) { f ->
                object : BasicFileAttributes {
                    override fun lastModifiedTime(): FileTime = FileTime.from(f.next())
                    override fun lastAccessTime(): FileTime = FileTime.from(f.next())
                    override fun creationTime(): FileTime = FileTime.from(f.next())
                    override fun isRegularFile(): Boolean = f.nextBoolean()
                    override fun isDirectory(): Boolean = f.nextBoolean()
                    override fun isSymbolicLink(): Boolean = f.nextBoolean()
                    override fun isOther(): Boolean = f.nextBoolean()
                    override fun size(): Long = f.nextLong()
                    override fun fileKey(): Any = f.nextString()
                }
            }
        }

    @MockK
    private lateinit var pictureRepository: PictureRepository

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

        val savedPicture = slot<Picture>()

        every { fileOperations.exists(any()) } returnsMonoOf true
        every { pictureRepository.existsByLocation(location) } returnsMonoOf false
        every { pictureRepository.save(capture(savedPicture)) }.returnsArgumentAsMono()
        every { applicationEventPublisher.publishEvent(any<PictureCreatedEvent>()) } just runs

        StepVerifier.create(pictureService.addPicture(location, name))
            .expectNextCount(1)
            .verifyComplete()

        verify {
            fileOperations.exists(any())
            pictureRepository.existsByLocation(location)
            applicationEventPublisher.publishEvent(match<PictureCreatedEvent> { it.picture == savedPicture.captured })
        }

        assertEquals(location, savedPicture.captured.location)
        assertEquals(name, savedPicture.captured.name)
    }

    @Test
    internal fun `should not add picture when file not exists`() {
        every { fileOperations.exists(any()) } returnsMonoOf false
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf false

        StepVerifier.create(pictureService.addPicture("location", "name"))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should not add picture when location already in db`() {
        every { fileOperations.exists(any()) } returnsMonoOf true
        every { pictureRepository.existsByLocation(any()) } returnsMonoOf true

        StepVerifier.create(pictureService.addPicture("location", "name"))
            .verifyError<ValidationException>()

        verify { pictureRepository.save(any()) wasNot Called }
    }

    @Test
    internal fun `should delete picture`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.deleteById(picture.id!!) }.returnsVoidMono()

        StepVerifier.create(pictureService.deletePicture(picture.id!!))
            .verifyComplete()

        verify {
            pictureRepository.deleteById(picture.id!!)
            fileOperations.deleteIfExists(any()) wasNot Called
        }
    }

    @Test
    internal fun `should delete picture and from disk`() {
        val picture = fixture.next<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture
        every { pictureRepository.deleteById(picture.id!!) }.returnsVoidMono()
        every { fileOperations.deleteIfExists(any()) } returnsMonoOf true

        StepVerifier.create(pictureService.deletePicture(picture.id!!, true))
            .verifyComplete()

        verify {
            pictureRepository.deleteById(picture.id!!)
            fileOperations.deleteIfExists(picture.location.toPath())
        }
    }

    @Test
    internal fun `should add tag to picture`() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureRepository.addTag(pictureId, tagId) }.returnsVoidMono()

        StepVerifier.create(pictureService.tagPictureBy(pictureId, tagId))
            .verifyComplete()

        verify { pictureRepository.addTag(pictureId, tagId) }
    }

    @Test
    internal fun `should remove tag from picture`() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureRepository.removeTag(pictureId, tagId) }.returnsVoidMono()

        StepVerifier.create(pictureService.removeTagFromPicture(pictureId, tagId))
            .verifyComplete()

        verify { pictureRepository.removeTag(pictureId, tagId) }
    }
}
