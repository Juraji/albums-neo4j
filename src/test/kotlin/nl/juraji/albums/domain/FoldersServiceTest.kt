package nl.juraji.albums.domain

import com.marcellogalhardo.fixture.next
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import nl.juraji.albums.configuration.TestFixtureConfiguration
import nl.juraji.albums.domain.folders.Folder
import nl.juraji.albums.domain.folders.FolderTreeView
import nl.juraji.albums.domain.folders.FoldersRepository
import nl.juraji.albums.util.*
import nl.juraji.reactor.validations.ValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class FoldersServiceTest {
    private val fixture = TestFixtureConfiguration.testFixture()

    @MockK
    private lateinit var foldersRepository: FoldersRepository

    @InjectMockKs
    private lateinit var foldersService: FoldersService

    @Test
    fun `should get tree`() {
        val root = fixture.next<Folder>()
        val level1 = fixture.next<Folder>()
        val level2 = fixture.next<Folder>()

        val expected = FolderTreeView(
            id = root.id!!,
            name = root.name,
            children = listOf(
                FolderTreeView(
                    id = level1.id!!,
                    name = level1.name,
                    children = listOf(
                        FolderTreeView(
                            id = level2.id!!,
                            name = level2.name,
                            children = emptyList(),
                            isRoot = false
                        )
                    ),
                    isRoot = false
                )
            ),
            isRoot = true
        )

        every { foldersRepository.findRoots() } returnsFluxOf root
        every { foldersRepository.findChildren(any()) }.returnsEmptyFlux()
        every { foldersRepository.findChildren(root.id!!) } returnsFluxOf level1
        every { foldersRepository.findChildren(level1.id!!) } returnsFluxOf level2

        val result = foldersService.getTree()

        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()

        verify { foldersRepository.findRoots() }
    }

    @Test
    fun `should create folder`() {
        val folder = fixture.next<Folder>()
        val expected = folder.copy(id = null)

        every { foldersRepository.save(any()) } returnsMonoOf folder

        val result = foldersService.createFolder(folder, "")

        StepVerifier.create(result)
            .expectNext(folder)
            .verifyComplete()

        verify { foldersRepository.save(expected) }
    }

    @Test
    fun `should create folder with parent id`() {
        val folder = fixture.next<Folder>()
        val parentId = fixture.nextString()
        val expected = folder.copy(id = null)

        every { foldersRepository.existsById(any<String>()) } returnsMonoOf true
        every { foldersRepository.save(any()) } returnsMonoOf folder
        every { foldersRepository.setParent(any(), any()) } returnsMonoOf folder

        val result = foldersService.createFolder(folder, parentId)

        StepVerifier.create(result)
            .expectNext(folder)
            .verifyComplete()

        verify {
            foldersRepository.existsById(parentId)
            foldersRepository.save(expected)
            foldersRepository.setParent(folder.id!!, parentId)
        }
    }

    @Test
    fun `should create folder with parent id where parent not exists`() {
        val folder = fixture.next<Folder>()
        val parentId = fixture.nextString()

        every { foldersRepository.existsById(any<String>()) } returnsMonoOf false

        val result = foldersService.createFolder(folder, parentId)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            foldersRepository.existsById(parentId)
            foldersRepository.save(any()) wasNot Called
            foldersRepository.setParent(any(), any()) wasNot Called
        }
    }

    @Test
    fun `should update folder`() {
        val folderId = fixture.nextString()
        val existing = fixture.next<Folder>().copy(id = folderId)
        val update = fixture.next<Folder>().copy(id = folderId)

        every { foldersRepository.findById(any<String>()) } returnsMonoOf existing
        every { foldersRepository.save(any()) }.returnsArgumentAsMono()

        val result = foldersService.updateFolder(folderId, update)

        StepVerifier.create(result)
            .expectNext(update)
            .verifyComplete()

        verify {
            foldersRepository.findById(folderId)
            foldersRepository.save(update)
        }
    }

    @Test
    fun `should delete folder`() {
        val folderId = fixture.nextString()

        every { foldersRepository.isEmptyById(any()) } returnsMonoOf true
        every { foldersRepository.deleteRecursivelyById(any()) }.returnsEmptyMono()

        val result = foldersService.deleteFolder(folderId, false)

        StepVerifier.create(result).verifyComplete()

        verify {
            foldersRepository.isEmptyById(folderId)
            foldersRepository.deleteRecursivelyById(folderId)
        }
    }

    @Test
    fun `should not delete non-empty folder`() {
        val folderId = fixture.nextString()

        every { foldersRepository.isEmptyById(any()) } returnsMonoOf false

        val result = foldersService.deleteFolder(folderId, false)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            foldersRepository.isEmptyById(folderId)
            foldersRepository.deleteById(any<String>()) wasNot Called
        }
    }

    @Test
    fun `should delete non-empty folder when recursive is set`() {
        val folderId = fixture.nextString()

        every { foldersRepository.deleteRecursivelyById(any()) }.returnsEmptyMono()

        val result = foldersService.deleteFolder(folderId, true)

        StepVerifier.create(result).verifyComplete()

        verify {
            foldersRepository.isEmptyById(any()) wasNot Called
            foldersRepository.deleteRecursivelyById(folderId)
        }
    }

    @Test
    fun `should move folder`() {
        val folderId = fixture.nextString()
        val targetId = fixture.nextString()
        val expected = fixture.next<Folder>()

        every { foldersRepository.existsById(any<String>()) } returnsMonoOf true
        every { foldersRepository.setParent(any(), any()) } returnsMonoOf expected

        val result = foldersService.moveFolder(folderId, targetId)

        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()

        verify {
            foldersRepository.existsById(folderId)
            foldersRepository.existsById(targetId)
            foldersRepository.setParent(folderId, targetId)
        }
    }

    @Test
    fun `should not move folder when folder does not exist`() {
        val folderId = fixture.nextString()
        val targetId = fixture.nextString()

        every { foldersRepository.existsById(folderId) } returnsMonoOf false
        every { foldersRepository.existsById(targetId) } returnsMonoOf true

        val result = foldersService.moveFolder(folderId, targetId)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            foldersRepository.existsById(folderId)
            foldersRepository.setParent(folderId, targetId) wasNot Called
        }
    }

    @Test
    fun `should not move folder when target does not exist`() {
        val folderId = fixture.nextString()
        val targetId = fixture.nextString()

        every { foldersRepository.existsById(folderId) } returnsMonoOf true
        every { foldersRepository.existsById(targetId) } returnsMonoOf false

        val result = foldersService.moveFolder(folderId, targetId)

        StepVerifier.create(result)
            .expectError<ValidationException>()
            .verify()

        verify {
            foldersRepository.existsById(targetId)
            foldersRepository.setParent(folderId, targetId) wasNot Called
        }
    }
}
