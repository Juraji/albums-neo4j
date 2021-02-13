package nl.juraji.albums.eventListeners

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.DirectoryService
import nl.juraji.albums.domain.directories.DirectoryCreatedEvent
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
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
    private lateinit var directoryService: DirectoryService

    @SpykBean
    private lateinit var directoryCreatedEventListener: DirectoryCreatedEventListener

    @Test
    fun `should link to parent directory`() {
        val event = DirectoryCreatedEvent(fixture.nextString())

        // Mock other listeners in component
        every { directoryCreatedEventListener.linkToChildDirectories(any()) } just runs

        every { directoryService.findAndLinkParent(any()) } returnsMonoOf Unit

        publisher.publishEvent(event)

        verify { directoryService.findAndLinkParent(event.directoryId) }
    }

    @Test
    fun `should link to child directories`() {
        val event = DirectoryCreatedEvent(fixture.nextString())

        // Mock other listeners in component
        every { directoryCreatedEventListener.linkToParentDirectory(any()) } just runs

        every { directoryService.findAndLinkChildren(any()) } returnsFluxOf Unit

        publisher.publishEvent(event)

        verify { directoryService.findAndLinkChildren(event.directoryId) }
    }
}
