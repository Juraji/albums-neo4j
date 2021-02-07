package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.returnsVoidMono
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class DirectoryCreatedEventListenerTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @InjectMockKs
    private lateinit var directoryCreatedEventListener: DirectoryCreatedEventListener

    @Test
    internal fun `should link to parent directory`() {
        val parentDir = fixture.next<Directory>().copy(location = Paths.get("/some").toString())
        val dir = fixture.next<Directory>().copy(location = Paths.get("/some/location").toString())
        val event = DirectoryCreatedEvent(this, dir)

        every { directoryRepository.findByLocation(parentDir.location) } returnsMonoOf parentDir
        every { directoryRepository.addChild(parentDir.id!!, dir.id!!) }.returnsVoidMono()

        directoryCreatedEventListener.linkToParentDirectory(event)

        verify {
            directoryRepository.findByLocation(parentDir.location)
            directoryRepository.addChild(parentDir.id!!, dir.id!!)
        }
    }

    @Test
    internal fun `should link to child directories`() {
        val dir = fixture.next<Directory>().copy(location = Paths.get("/some/location").toString())
        val parent = fixture.next<Directory>().copy(location = Paths.get("/some").toString())
        val child1 = fixture.next<Directory>().copy(location = Paths.get("/some/location/child1").toString())
        val child2 = fixture.next<Directory>().copy(location = Paths.get("/some/location/child2").toString())
        val subChild = fixture.next<Directory>().copy(location = Paths.get("/some/location/child3/sub-child").toString())
        val startingWithResult = listOf(parent, dir, child1, child2,  subChild)
        val event = DirectoryCreatedEvent(this, dir)

        every { directoryRepository.findByLocationStartingWith(dir.location)} returnsFluxOf startingWithResult
        every { directoryRepository.addChild(dir.id!!, any()) }.returnsVoidMono()

        directoryCreatedEventListener.linkToChildDirectories(event)

        verify {
            directoryRepository.findByLocationStartingWith(dir.location)
            directoryRepository.addChild(dir.id!!, child1.id!!)
            directoryRepository.addChild(dir.id!!, child2.id!!)
            directoryRepository.addChild(dir.id!!, parent.id!!) wasNot Called
            directoryRepository.addChild(dir.id!!, dir.id!!) wasNot Called
            directoryRepository.addChild(dir.id!!, subChild.id!!) wasNot Called
        }
    }
}
