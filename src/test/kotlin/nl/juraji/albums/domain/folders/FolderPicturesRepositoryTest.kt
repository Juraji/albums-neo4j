package nl.juraji.albums.domain.folders

import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.util.BaseTestHarnessConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.neo4j.core.Neo4jClient
import reactor.test.StepVerifier

@DataNeo4jTest
class FolderPicturesRepositoryTest {

    @Autowired
    private lateinit var folderPicturesRepository: FolderPicturesRepository

    @Autowired
    private lateinit var neo4jClient: Neo4jClient

    @Test
    internal fun `should add picture to folder`() {
        val returnValue = folderPicturesRepository.addPictureToFolder("root", "picture1")

        StepVerifier.create(returnValue)
            .expectNextMatches { it.id == "picture1" }
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (:Folder {id:'root'})-[:HAS_PICTURE]->(p:Picture {id:'picture1'})
            RETURN p
        """
        )
            .fetch()
            .all()

        assertEquals(1, result.size)
    }

    @TestConfiguration
    class TestHarnessConfig : BaseTestHarnessConfig() {
        // language=Cypher
        override fun withFixtureQuery(): String = """
            CREATE (root:Folder {
                id: 'root',
                name: 'Root'
            })
            
            CREATE (picture:Picture {
                id: 'picture1',
                name: 'Picture 1',
                type: 'UNKNOWN',
                width: 0,
                height: 0,
                fileSize: 0,
                addedOn: localdatetime('2021-03-25T19:49:00'),
                lastModified: localdatetime('2021-03-25T19:49:00')
            })
        """.trimIndent()

    }
}
