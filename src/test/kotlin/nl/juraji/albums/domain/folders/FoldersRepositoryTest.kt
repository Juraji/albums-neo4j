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
        val returnValue = foldersRepository.setParent("unlinkedFolder", "root")

        StepVerifier.create(returnValue)
            .expectNext(Folder(id = "unlinkedFolder", name = "Unlinked folder"))
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (:Folder {id: 'root'})-[:HAS_CHILD]->(child)
            RETURN child
            """
        )
            .fetch()
            .all()

        assertEquals(2, result.size)
    }

    @Test
    internal fun `should get roots`() {
        val result = foldersRepository.getRoots()

        StepVerifier.create(result)
            .expectNext(Folder(id = "root", name="Root"))
            .verifyComplete()
    }

    @TestConfiguration
    class TestHarnessConfig : BaseTestHarnessConfig() {
        // language=Cypher
        override fun withFixtureQuery(): String = """
            CREATE (root:Folder {
                id: 'root',
                name: 'Root'
            })
            
            CREATE (linkedFolder:Folder {
                id: 'linkedFolder',
                name: 'Linked folder'
            })
            MERGE (root)-[:HAS_CHILD]->(linkedFolder)
            
            CREATE (unlinkedFolder:Folder {
                id: 'unlinkedFolder',
                name: 'Unlinked folder'
            })
        """.trimIndent()

    }
}
