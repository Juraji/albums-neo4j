package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.andThenMonoOf
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.returnsVoidMono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class DuplicatesServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @InjectMockKs
    private lateinit var duplicatesService: DuplicatesService

    @Test
    internal fun `should add DUPLICATED_BY relationship from source to target`() {
        val source = fixture.next<Picture>()
        val target = fixture.next<Picture>()
        val similarity = 0.86
        val savedPicture = slot<Picture>()

        every { pictureRepository.findById(any() as String) } returnsMonoOf source andThenMonoOf target
        every { pictureRepository.save(capture(savedPicture)) }.returnsArgumentAsMono()

        StepVerifier.create(duplicatesService.markDuplicatePicture(source.id!!, target.id!!, similarity))
            .expectNextCount(1)
            .verifyComplete()

        assertEquals(source.id, savedPicture.captured.id)
        assertEquals(1, savedPicture.captured.duplicates.count { it.picture == target })
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship from source to target`() {
        val pictureId = fixture.nextString()
        val targetId = fixture.nextString()

        every { pictureRepository.removeDuplicatedBy(pictureId, targetId) }.returnsVoidMono()

        StepVerifier.create(duplicatesService.removeDuplicateFromPicture(pictureId, targetId))
            .verifyComplete()
    }
}
