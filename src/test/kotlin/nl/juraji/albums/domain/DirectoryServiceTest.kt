package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.toPath
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class DirectoryServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var fileOperations: FileOperations

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
        val postedDirectory = fixture.next<Directory>()
            .copy(id = null, location = Paths.get("/some/location").toString(), name = "location")
        val expected = postedDirectory.copy(id = fixture.nextString())

        every { directoryRepository.save(postedDirectory) } returnsMonoOf expected
        every { applicationEventPublisher.publishEvent(any<DirectoryCreatedEvent>()) } just runs

        StepVerifier.create(directoryService.createDirectory(postedDirectory.location))
            .expectNext(expected)
            .verifyComplete()

        verify {
            directoryRepository.save(postedDirectory)
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == expected.id })
        }
    }

    @Test
    internal fun `should create directory recursively`() {
        val rootPath = "/some/location".toPath()
        val child1Path = "/some/location/child1".toPath()
        val child2Path = "/some/location/child2".toPath()
        val child2Sub1Path = "/some/location/child2/sub1".toPath()
        val existingChildPath = "/some/location/existing-child".toPath()
        val directoryList = listOf(rootPath, child1Path, child2Path, child2Sub1Path, existingChildPath)

        every { fileOperations.listDirectories(any(), any()) } returnsFluxOf directoryList
        every { directoryRepository.existsByLocation(any()) } returnsMonoOf false
        every { directoryRepository.existsByLocation(existingChildPath.toString()) } returnsMonoOf true
        every { directoryRepository.save(any()) } answers {
            val s = firstArg<Directory>()
            s.copy(id = s.name).toMono()
        }
        every { applicationEventPublisher.publishEvent(any<DirectoryCreatedEvent>()) } just runs

        StepVerifier.create(directoryService.createDirectory(rootPath.toString(), true))
            .expectNextMatches { it.location == rootPath.toString() }
            .verifyComplete()


        verify(exactly = 5) { directoryRepository.existsByLocation(any()) }
        verify(exactly = 0) {
            directoryRepository.save(match { it.location == existingChildPath.toString() })
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == "existing-child" })
        }
        verify {
            fileOperations.listDirectories(rootPath, true)
            directoryRepository.save(match { it.location == rootPath.toString() })
            directoryRepository.save(match { it.location == child1Path.toString() })
            directoryRepository.save(match { it.location == child2Path.toString() })
            directoryRepository.save(match { it.location == child2Sub1Path.toString() })
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == "location" })
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == "child1" })
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == "child2" })
            applicationEventPublisher.publishEvent(match<DirectoryCreatedEvent> { it.directoryId == "sub1" })
        }
    }

    @Test
    internal fun `should find and link parent`() {
        val directory = fixture.next<Directory>().copy(location = "/some/location".toPath().toString())
        val parentDirectory = fixture.next<Directory>().copy(location = "/some".toPath().toString())

        every { directoryRepository.findById(directory.id!!) } returnsMonoOf directory
        every { directoryRepository.findByLocation(any()) } returnsMonoOf parentDirectory
        every { directoryRepository.addChild(any(), any()) }.returnsEmptyMono()

        StepVerifier.create(directoryService.findAndLinkParent(directory.id!!))
            .verifyComplete()

        verify {
            directoryRepository.findById(directory.id!!)
            directoryRepository.findByLocation(parentDirectory.location)
            directoryRepository.addChild(parentDirectory.id!!, directory.id!!)
        }
    }

    @Test
    internal fun `should find and link children`() {
        val directory = fixture.next<Directory>().copy(location = "/self".toPath().toString())
        val child1 = fixture.next<Directory>().copy(location = "/self/child1".toPath().toString())
        val child2 = fixture.next<Directory>().copy(location = "/self/child2".toPath().toString())
        val childOfChild = fixture.next<Directory>().copy(location = "/self/child2/child".toPath().toString())

        every { directoryRepository.findById(directory.id!!) } returnsMonoOf directory
        every { directoryRepository.findByLocationStartingWith(any()) } returnsFluxOf listOf(
            directory, child1, child2, childOfChild
        )
        every { directoryRepository.addChild(any(), any()) }.returnsEmptyMono()

        StepVerifier.create(directoryService.findAndLinkChildren(directory.id!!))
            .verifyComplete()

        verify {
            directoryRepository.findByLocationStartingWith(directory.location)
            directoryRepository.addChild(directory.id!!, child1.id!!)
            directoryRepository.addChild(directory.id!!, child2.id!!)
        }
    }
}
