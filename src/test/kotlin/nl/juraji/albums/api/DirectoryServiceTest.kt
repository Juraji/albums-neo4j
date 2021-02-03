package nl.juraji.albums.api

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.model.Directory
import nl.juraji.albums.model.DirectoryDescription
import nl.juraji.albums.repositories.DirectoryRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class DirectoryServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @InjectMockKs
    private lateinit var directoryService: DirectoryService

    @Test
    internal fun `should get directories`() {
        val expected: List<DirectoryDescription> = listOf(fixture.next(), fixture.next(), fixture.next())

        every { directoryRepository.findAllDescriptions() } returnsFluxOf expected

        StepVerifier.create(directoryService.getAllDirectories())
            .expectNextSequence(expected)
            .verifyComplete()

        verify {
            directoryRepository.findAllDescriptions()
        }
    }

    @Test
    internal fun `should get directory by id`() {
        val expected = fixture.next<DirectoryDescription>()

        every { directoryRepository.findDescriptionById(expected.id) } returnsMonoOf expected

        StepVerifier.create(directoryService.getDirectory(expected.id))
            .expectNext(expected)
            .verifyComplete()

        verify {
            directoryRepository.findDescriptionById(expected.id)
        }
    }
}
