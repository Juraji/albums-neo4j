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
import nl.juraji.albums.model.PictureDescription
import nl.juraji.albums.model.Tag
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.repositories.PictureRepository
import nl.juraji.albums.repositories.TagRepository
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
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var tagRepository: TagRepository

    @MockK
    private lateinit var fileOperations: FileOperations

    @InjectMockKs
    private lateinit var pictureService: PictureService

    @Test
    internal fun `should get all pictures`() {
        val picture = fixture.next<PictureDescription>()

        every { pictureRepository.findAllDescriptions() } returnsFluxOf picture

        StepVerifier.create(pictureService.getAllPictures())
            .expectNext(picture)
            .verifyComplete()
    }

    @Test
    internal fun `should get picture by id`() {
        val picture = fixture.next<PictureDescription>()

        every { pictureRepository.findDescriptionById(picture.id) } returnsMonoOf picture

        StepVerifier.create(pictureService.getPicture(picture.id))
            .expectNext(picture)
            .verifyComplete()
    }

    @Test
    internal fun `should add picture`() {
        val savedDirectory = slot<Directory>()
        val savedPicture = slot<Picture>()

        every { fileOperations.exists(any()) } returnsMonoOf true
        every { fileOperations.getParentPathStr(any()) } returns "mock_path"
        every { fileOperations.readContentType(any()) } returnsMonoOf "image/jpeg"
        every { fileOperations.readAttributes(any()) } returnsMonoOf fixture.next()
        every { directoryRepository.findByLocation(any()) }.returnsEmptyMono()
        every { directoryRepository.save(capture(savedDirectory)) }.returnsArgumentAsMono()
        every { pictureRepository.existsByLocation("location") } returnsMonoOf false
        every { pictureRepository.save(capture(savedPicture)) }.returnsArgumentAsMono()

        StepVerifier.create(pictureService.addPicture("location", "name"))
            .expectNextCount(1)
            .verifyComplete()

        // Verify side-effect of adding to directory
        verify {
            fileOperations.getParentPathStr("location")
            directoryRepository.findByLocation("mock_path")
            directoryRepository.save(any())
        }

        assertEquals(savedPicture.captured, savedDirectory.captured.pictures[0].picture)
        assertEquals("location", savedPicture.captured.location)
        assertEquals("name", savedPicture.captured.name)
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
    internal fun `should add tag to picture`() {
        val tag = fixture.next<Tag>()
        val picture = fixture.next<Picture>()
        val savedPicture = slot<Picture>()

        every { pictureRepository.findById(picture.id!!) } returnsMonoOf picture
        every { tagRepository.findById(tag.id!!) } returnsMonoOf tag
        every { pictureRepository.save(capture(savedPicture)) } returnsMonoOf picture

        StepVerifier.create(pictureService.tagPictureBy(picture.id!!, tag.id!!))
            .expectNext(picture)
            .verifyComplete()

        assertEquals(tag, savedPicture.captured.tags[0].tag)
    }
}
