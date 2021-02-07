package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import reactor.test.StepVerifier
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class DirectoryServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var directoryService: DirectoryService

    @Test
    internal fun `should get directories`() {
        val expected: List<Directory> = listOf(fixture.next(), fixture.next(), fixture.next())

        every { directoryRepository.findAll() } returnsFluxOf expected

        StepVerifier.create(directoryService.getAllDirectories())
            .expectNextSequence(expected)
            .verifyComplete()

        verify { directoryRepository.findAll() }
    }

    @Test
    internal fun `should get directory by id`() {
        val expected = fixture.next<Directory>()

        every { directoryRepository.findById(expected.id!!) } returnsMonoOf expected

        StepVerifier.create(directoryService.getDirectory(expected.id!!))
            .expectNext(expected)
            .verifyComplete()

        verify { directoryRepository.findById(expected.id!!) }
    }

    @Test
    internal fun `should create directory`() {
        val parent = fixture.next<Directory>().copy(location = Paths.get("/some").toString())
        val postedDirectory = fixture.next<Directory>()
            .copy(id = null, location = Paths.get("/some/location").toString(), name = "location")
        val expected = postedDirectory.copy(id = fixture.nextString())

        every { directoryRepository.findByLocation(any()) } returnsMonoOf parent
        every { directoryRepository.save(postedDirectory) } returnsMonoOf expected
        every { applicationEventPublisher.publishEvent(any<DirectoryCreatedEvent>()) } just runs

        StepVerifier.create(directoryService.createDirectory(postedDirectory.location))
            .expectNext(expected)
            .verifyComplete()

        verify {
            directoryRepository.save(postedDirectory)
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directory == expected })
        }
    }
}
