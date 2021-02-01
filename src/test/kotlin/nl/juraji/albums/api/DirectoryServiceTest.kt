package nl.juraji.albums.api

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.model.Directory
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.services.FileSystemService
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class DirectoryServiceTest {
    private val fixture = Fixture()

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var fileSystemService: FileSystemService

    @InjectMockKs
    private lateinit var directoryService: DirectoryService

    @BeforeEach
    internal fun setUp() {
        fixture.register(Directory::class) { Directory(id = it.nextLong(), location = it.nextString()) }
    }

    @Test
    internal fun `should get directories`() {
        val expected: List<Directory> = listOf(fixture.next(), fixture.next(), fixture.next())

        every { directoryRepository.findAll() } returnsFluxOf expected

        StepVerifier.create(directoryService.getDirectories())
            .expectNextSequence(expected)
            .verifyComplete()

        verify {
            directoryRepository.findAll()
        }
    }

    @Test
    internal fun `should get directory by id`() {
        val expected = fixture.next<Directory>()

        every { directoryRepository.findById(1) } returnsMonoOf expected

        StepVerifier.create(directoryService.getDirectory(1))
            .expectNext(expected)
            .verifyComplete()

        verify {
            directoryRepository.findById(1)
        }
    }

    @Test
    internal fun `should create directory`() {
        val directory = fixture.next<Directory>()

        every { fileSystemService.exists(directory.location.toPath()) } returnsMonoOf true
        every { directoryRepository.save(directory) } returnsMonoOf directory

        StepVerifier.create(directoryService.createDirectory(directory))
            .expectNext(directory)
            .verifyComplete()

        verify {
            fileSystemService.exists(directory.location.toPath())
            directoryRepository.save(directory)
        }
    }

    @Test
    internal fun `should not create directory if location not exists in fs`() {
        val directory = fixture.next<Directory>()

        every { fileSystemService.exists(directory.location.toPath()) } returnsMonoOf false

        StepVerifier.create(directoryService.createDirectory(directory))
            .expectError(ValidationException::class.java)
            .verify()

        verify {
            fileSystemService.exists(directory.location.toPath())
        }
    }
}
