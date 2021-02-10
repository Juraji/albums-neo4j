package nl.juraji.albums.eventListeners

import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.returnsVoidMono
import nl.juraji.albums.util.toPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher

@SpringBootTest(classes = [DirectoryCreatedEventListener::class])
internal class DirectoryCreatedEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @MockkBean
    private lateinit var directoryRepository: DirectoryRepository

    @SpykBean
    private lateinit var directoryCreatedEventListener: DirectoryCreatedEventListener

    @Test
    fun `should link to parent directory`() {
        val eventDirectory = fixture.next<Directory>().copy(location = "/some/location".toPath().toString())
        val parentDirectory = fixture.next<Directory>().copy(location = "/some".toPath().toString())
        val event = DirectoryCreatedEvent(this, eventDirectory)

        // Mock other listeners in component
        every {directoryCreatedEventListener.linkToChildDirectories(any()) } just runs

        every { directoryRepository.findByLocation(any()) } returnsMonoOf parentDirectory
        every { directoryRepository.addChild(any(), any()) }.returnsVoidMono()

        publisher.publishEvent(event)

        verify {
            directoryRepository.findByLocation(parentDirectory.location)
            directoryRepository.addChild(parentDirectory.id!!, eventDirectory.id!!)
        }
    }

    @Test
    fun `should link to child directories`() {
        val directory = fixture.next<Directory>().copy(location = "/self".toPath().toString())
        val child1 = fixture.next<Directory>().copy(location = "/self/child1".toPath().toString())
        val child2 = fixture.next<Directory>().copy(location = "/self/child2".toPath().toString())
        val childOfChild = fixture.next<Directory>().copy(location = "/self/child2/child".toPath().toString())

        // Mock other listeners in component
        every {directoryCreatedEventListener.linkToParentDirectory(any()) } just runs

        every { directoryRepository.findByLocationStartingWith(any()) } returnsFluxOf listOf(
            directory, child1, child2, childOfChild
        )
        every { directoryRepository.addChild(any(), any()) }.returnsVoidMono()

        val event = DirectoryCreatedEvent(this, directory)

        publisher.publishEvent(event)

        verify {
            directoryRepository.findByLocationStartingWith(directory.location)
            directoryRepository.addChild(directory.id!!, child1.id!!)
            directoryRepository.addChild(directory.id!!, child2.id!!)
        }
    }
}
