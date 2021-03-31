package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.domain.tags.TagsRepository
import nl.juraji.albums.util.returnsArgumentAsMono
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class TagsServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var tagsRepository: TagsRepository

    @InjectMockKs
    private lateinit var tagsService: TagsService

    @Test
    fun `should get all tags`() {
        val tags = fixture.nextListOf<Tag>()

        every { tagsRepository.findAll() } returnsFluxOf tags

        StepVerifier.create(tagsService.getAllTags())
            .expectNextSequence(tags)
            .verifyComplete()

        verify { tagsRepository.findAll() }
    }

    @Test
    fun `should create tag`() {
        val tag = fixture.next<Tag>()

        every { tagsRepository.existsByLabel(any()) } returnsMonoOf false
        every { tagsRepository.save(any()) }.returnsArgumentAsMono()

        val result = tagsService.createTag(tag)

        StepVerifier.create(result)
            .expectNext(tag)
            .verifyComplete()

        verify {
            tagsRepository.existsByLabel(tag.label)
            tagsRepository.save(tag)
        }
    }

    @Test
    fun `should not create duplicate tag label`() {
        val tag = fixture.next<Tag>()

        every { tagsRepository.existsByLabel(any()) } returnsMonoOf true

        val result = tagsService.createTag(tag)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            tagsRepository.existsByLabel(tag.label)
            tagsRepository.save(any()) wasNot Called
        }
    }

    @Test
    fun `should update tag`() {
        val existing = fixture.next<Tag>()
        val update = existing.copy(
            id = "Something else",
            label = "New label"
        )
        val expected = existing.copy(
            label = "New label"
        )

        every { tagsRepository.findById(any<String>()) } returnsMonoOf existing
        every { tagsRepository.existsByLabel(any()) } returnsMonoOf false
        every { tagsRepository.save(any()) }.returnsArgumentAsMono()

        val result = tagsService.updateTag(existing.id!!, update)

        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()

        verify {
            tagsRepository.findById(existing.id!!)
            tagsRepository.existsByLabel(update.label)
            tagsRepository.save(expected)
        }
    }

    @Test
    fun `should not update tag when label exists`() {
        val existing = fixture.next<Tag>()
        val update = existing.copy(
            label = "New label"
        )

        every { tagsRepository.findById(any<String>()) } returnsMonoOf existing
        every { tagsRepository.existsByLabel(any()) } returnsMonoOf true

        val result = tagsService.updateTag(existing.id!!, update)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            tagsRepository.findById(existing.id!!)
            tagsRepository.existsByLabel(update.label)
            tagsRepository.save(any()) wasNot Called
        }
    }

    @Test
    fun `should delete tag`() {
        val tagId = fixture.nextString()

        every { tagsRepository.deleteById(any<String>()) }.returnsEmptyMono()

        val result = tagsService.deleteTag(tagId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { tagsRepository.deleteById(tagId) }
    }
}
