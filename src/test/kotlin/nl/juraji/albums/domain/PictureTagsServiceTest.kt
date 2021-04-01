package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.nextListOf
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.pictures.PictureTagsRepository
import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.util.returnsEmptyMono
import nl.juraji.albums.util.returnsFluxOf
import nl.juraji.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class PictureTagsServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var pictureTagsRepository: PictureTagsRepository

    @InjectMockKs
    private lateinit var pictureTagsService: PictureTagsService

    @Test
    fun getPictureTags() {
        val pictureId = fixture.nextString()
        val tags = fixture.nextListOf<Tag>()

        every { pictureTagsRepository.findPictureTags(any()) } returnsFluxOf tags

        val result = pictureTagsService.getPictureTags(pictureId)

        StepVerifier.create(result)
            .expectNextSequence(tags)
            .verifyComplete()

        verify { pictureTagsRepository.findPictureTags(pictureId) }
    }

    @Test
    fun addTagToPicture() {
        val pictureId = fixture.nextString()
        val tag = fixture.next<Tag>()

        every { pictureTagsRepository.addTagToPicture(any(), any()) } returnsMonoOf tag

        val result = pictureTagsService.addTagToPicture(pictureId, tag.id!!)

        StepVerifier.create(result)
            .expectNext(tag)
            .verifyComplete()

        every { pictureTagsRepository.addTagToPicture(pictureId, tag.id!!) }
    }

    @Test
    fun removeTagFromPicture() {
        val pictureId = fixture.nextString()
        val tagId = fixture.nextString()

        every { pictureTagsRepository.removeTagFromPicture(any(), any()) }.returnsEmptyMono()

        val result = pictureTagsService.removeTagFromPicture(pictureId, tagId)

        StepVerifier.create(result)
            .verifyComplete()

        every { pictureTagsRepository.removeTagFromPicture(pictureId, tagId) }
    }
}
