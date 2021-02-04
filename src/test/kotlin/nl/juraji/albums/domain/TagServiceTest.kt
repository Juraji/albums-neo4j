package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configurations.TestFixtureConfiguration
import nl.juraji.albums.domain.TagService
import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.domain.tags.TagRepository
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import nl.juraji.albums.util.returnsVoidMono
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.verifyError
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class TagServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var tagRepository: TagRepository

    @InjectMockKs
    private lateinit var tagService: TagService

    @Test
    internal fun `should get all tags`() {
        val tag = fixture.next<Tag>()
        every { tagRepository.findAll() } returnsFluxOf tag

        StepVerifier.create(tagService.getAllTags())
            .expectNext(tag)
            .verifyComplete()
    }

    @Test
    internal fun `should create tag`() {
        val tag = fixture.next<Tag>().copy(id = null)

        every { tagRepository.existsByLabel(tag.label) } returnsMonoOf false
        every { tagRepository.save(tag) } returnsMonoOf tag

        StepVerifier.create(tagService.createTag(tag.label, tag.color))
            .expectNext(tag)
            .verifyComplete()

        verify { tagRepository.save(tag) }
    }

    @Test
    internal fun `should not persist tag if label exists`() {
        val tag = fixture.next<Tag>().copy(id = null)

        every { tagRepository.existsByLabel(tag.label) } returnsMonoOf true

        StepVerifier.create(tagService.createTag(tag.label, tag.color))
            .verifyError<ValidationException>()

        verify { tagRepository.save(tag) wasNot Called }
    }

    @Test
    internal fun `should delete tag`() {
        val tagId = fixture.nextString()

        every { tagRepository.deleteById(tagId) }.returnsVoidMono()

        StepVerifier.create(tagService.deleteTag(tagId))
            .verifyComplete()

        verify { tagRepository.deleteById(tagId) }
    }
}
