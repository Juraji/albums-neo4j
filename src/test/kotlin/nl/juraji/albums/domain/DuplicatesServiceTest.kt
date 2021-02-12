package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.duplicates.Duplicate
import nl.juraji.albums.domain.duplicates.DuplicateRepository
import nl.juraji.albums.domain.duplicates.DuplicatedBy
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class DuplicatesServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var duplicateRepository: DuplicateRepository

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @InjectMockKs
    private lateinit var duplicatesService: DuplicatesService

    @Test
    fun `should find all duplicates`() {
        val duplicates: List<Duplicate> = fixture.next()

        every { duplicateRepository.findAll() } returnsFluxOf duplicates

        StepVerifier.create(duplicatesService.findAllDuplicates())
            .expectNextSequence(duplicates)
            .verifyComplete()
    }

    @Test
    fun `should set duplicate picture`() {
        val source = fixture.next<Picture>()
        val target = fixture.next<Picture>()
        val similarity = fixture.nextFloat()
        val matchedOn = fixture.next<LocalDateTime>()

        val expected = Duplicate(source = source, target = target, similarity = similarity, matchedOn = matchedOn)

        every { duplicateRepository.existsBySourceIdAndTargetId(any(), any()) } returnsMonoOf false
        every { pictureRepository.findById(source.id!!) } returnsMonoOf source
        every { pictureRepository.findById(target.id!!) } returnsMonoOf target
        every { duplicateRepository.save(any()) }.returnsArgumentAsMono()

        StepVerifier.create(duplicatesService.setDuplicatePicture(source.id!!, target.id!!, similarity, matchedOn))
            .expectNext(expected)
            .verifyComplete()

        verify {
            duplicateRepository.existsBySourceIdAndTargetId(source.id!!, target.id!!)
            duplicateRepository.existsBySourceIdAndTargetId(target.id!!, source.id!!)
            pictureRepository.findById(source.id!!)
            pictureRepository.findById(target.id!!)
            duplicateRepository.save(expected)
        }
    }

    @Test
    fun `should find by picture id`() {
        val pictureId = fixture.nextString()
        val expected: List<DuplicatedBy> = fixture.next()

        every { duplicateRepository.findBySourceId(pictureId) } returnsFluxOf expected

        StepVerifier.create(duplicatesService.findByPictureId(pictureId))
            .expectNextSequence(expected)
            .verifyComplete()
    }

    @Test
    fun `should delete by id`() {
        val duplicateId = fixture.nextString()

        every { duplicateRepository.deleteById(duplicateId) }.returnsEmptyMono()

        StepVerifier.create(duplicatesService.deleteById(duplicateId))
            .verifyComplete()
    }
}
