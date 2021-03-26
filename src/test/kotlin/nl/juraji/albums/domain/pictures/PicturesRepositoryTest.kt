package nl.juraji.albums.domain.pictures

import nl.juraji.albums.util.BaseTestHarnessConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.neo4j.core.Neo4jClient
import reactor.test.StepVerifier

@DataNeo4jTest
class PicturesRepositoryTest {


    @Autowired
    private lateinit var picturesRepository: PicturesRepository

    @Autowired
    private lateinit var neo4jClient: Neo4jClient

    @Test
    internal fun `should add picture to folder`() {
        val returnValue = picturesRepository.addPictureToFolder("root", "picture2")

        StepVerifier.create(returnValue)
            .expectNextMatches { it.id == "picture2" }
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (:Folder {id:'root'})-[:HAS_PICTURE]->(p:Picture {id:'picture2'})
            RETURN p
        """
        )
            .fetch()
            .all()

        Assertions.assertEquals(1, result.size)
    }

    @Test
    fun `should find all by folder id`(){
        val result = picturesRepository.findAllByFolderId("root")

        StepVerifier.create(result)
            .expectNextMatches { it.id == "picture1" }
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
            
            CREATE (picture1:Picture {
                id: 'picture1',
                name: 'Picture 1',
                type: 'UNKNOWN',
                width: 0,
                height: 0,
                fileSize: 0,
                addedOn: localdatetime('2021-03-25T19:49:00')
            })<-[:HAS_PICTURE]-(root)
            
            CREATE (picture2:Picture {
                id: 'picture2',
                name: 'Picture 2',
                type: 'UNKNOWN',
                width: 0,
                height: 0,
                fileSize: 0,
                addedOn: localdatetime('2021-03-25T19:49:00')
            })
        """.trimIndent()

    }
}
