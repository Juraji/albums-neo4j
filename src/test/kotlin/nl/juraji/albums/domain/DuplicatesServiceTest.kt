package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.domain.relationships.DuplicatedBy
import nl.juraji.albums.domain.relationships.DuplicatedByRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsVoidMono
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class DuplicatesServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var duplicatedByRepository: DuplicatedByRepository

    @InjectMockKs
    private lateinit var duplicatesService: DuplicatesService

    @Test
    internal fun `should find DUPLICATED_BY by picture id`() {
        val pictureId = fixture.nextString()
        val duplicates: List<DuplicatedBy> = fixture.next()

        every { duplicatedByRepository.findByPictureId(pictureId) } returnsFluxOf duplicates

        StepVerifier.create(duplicatesService.findDuplicatedByByPictureId(pictureId))
            .expectNext(*duplicates.toTypedArray())
            .verifyComplete()

        verify { duplicatedByRepository.findByPictureId(pictureId) }
    }

    @Test
    internal fun `should add DUPLICATED_BY relationship from source to target`() {
        val sourceId = fixture.nextString()
        val targetId = fixture.nextString()
        val similarity = 0.86
        val matchedOn = LocalDateTime.now()

        every { pictureRepository.addDuplicatedBy(sourceId, targetId, similarity, matchedOn) }.returnsVoidMono()

        StepVerifier.create(duplicatesService.setDuplicatePicture(sourceId, targetId, similarity, matchedOn))
            .verifyComplete()

        verify { pictureRepository.addDuplicatedBy(sourceId, targetId, similarity, matchedOn) }
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship from source to target`() {
        val pictureId = fixture.nextString()
        val targetId = fixture.nextString()

        every { pictureRepository.removeDuplicatedBy(pictureId, targetId) }.returnsVoidMono()

        StepVerifier.create(duplicatesService.unsetDuplicatePicture(pictureId, targetId))
            .verifyComplete()

        verify { pictureRepository.removeDuplicatedBy(pictureId, targetId) }
    }
}
