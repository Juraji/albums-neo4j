package nl.juraji.albums.domain.folders

import nl.juraji.albums.util.BaseTestHarnessConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.neo4j.core.Neo4jClient
import reactor.test.StepVerifier

@DataNeo4jTest
class FoldersRepositoryTest {

    @Autowired
    private lateinit var foldersRepository: FoldersRepository

    @Autowired
    private lateinit var neo4jClient: Neo4jClient

    @Test
    internal fun `should set parent`() {
        val returnValue = foldersRepository.setParent("folder2", "root1")

        StepVerifier.create(returnValue)
            .expectNext(Folder(id = "root1", name = "Root 1"))
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (p:Folder)-[:HAS_CHILD]->(:Folder {id: 'folder2'})
            RETURN p
            """
        )
            .fetch()
            .all()

        assertEquals(1, result.size)
    }

    @Test
    internal fun `should get roots`() {
        val result = foldersRepository.findRoots()

        StepVerifier.create(result)
            .expectNext(Folder(id = "root1", name="Root 1"))
            .expectNext(Folder(id = "root2", name="Root 2"))
            .verifyComplete()
    }

    @Test
    fun `should check is empty by id`() {
        val result = foldersRepository.isEmptyById("folder1")

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `should check is not empty by id`() {
        val result = foldersRepository.isEmptyById("root1")

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }

    @TestConfiguration
    class TestHarnessConfig : BaseTestHarnessConfig() {
        // language=Cypher
        override fun withFixtureQuery(): String = """
            CREATE (root1:Folder {
                id: 'root1',
                name: 'Root 1'
            })
            
            CREATE (root2:Folder {
                id: 'root2',
                name: 'Root 2'
            })
            
            CREATE (folder1:Folder {
                id: 'folder1',
                name: 'Folder 1'
            })<-[:HAS_CHILD]-(root1)
            
            CREATE (folder2:Folder {
                id: 'folder2',
                name: 'Folder 2'
            })<-[:HAS_CHILD]-(root2)
        """.trimIndent()

    }
}
