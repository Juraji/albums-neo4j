package nl.juraji.albums.domain.pictures

import nl.juraji.albums.util.BaseTestHarnessConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.neo4j.core.Neo4jClient
import reactor.test.StepVerifier

@DataNeo4jTest
class PictureTagsRepositoryTest {

    @Autowired
    private lateinit var pictureTagsRepository: PictureTagsRepository

    @Autowired
    private lateinit var neo4jClient: Neo4jClient

    @Test
    @Order(1)
    fun `should find picture tags`() {
        val result = pictureTagsRepository.findPictureTags("picture1")

        StepVerifier.create(result)
            .expectNextMatches { it.id == "tag1" }
            .expectNextMatches { it.id == "tag2" }
            .expectNextMatches { it.id == "tag3" }
            .verifyComplete()
    }

    @Test
    @Order(2)
    fun `should add tag to picture`() {
        val returnValue = pictureTagsRepository.addTagToPicture("picture1", "tag3")

        StepVerifier.create(returnValue)
            .expectNextMatches { it.id == "tag3" }
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (:Picture {id: 'picture1'})-[:HAS_TAG]->(t:Tag {id: 'tag3'})
            RETURN t
        """
        )
            .fetch()
            .all()

        assertEquals(1, result.size)
    }

    @Test
    @Order(3)
    fun `should remove tag from picture`() {
        val returnValue = pictureTagsRepository.removeTagFromPicture("picture1", "tag2")

        StepVerifier.create(returnValue)
            .verifyComplete()

        val result = neo4jClient.query(
            """
            MATCH (:Picture {id: 'picture1'})-[:HAS_TAG]->(t:Tag {id: 'tag2'})
            RETURN t
        """
        )
            .fetch()
            .all()

        assertEquals(0, result.size)
    }

    @TestConfiguration
    class TestHarnessConfig : BaseTestHarnessConfig() {
        // language=Cypher
        override fun withFixtureQuery(): String = """
            CREATE (picture:Picture {
                id: 'picture1',
                name: 'Picture 1',
                type: 'UNKNOWN',
                width: 0,
                height: 0,
                fileSize: 0,
                addedOn: localdatetime('2021-03-25T19:49:00')
            })
            
            CREATE (tag1:Tag {
                id: 'tag1',
                label: 'Tag 1',
                textColor: '#000000',
                color: '#ffffff'
            })<-[:HAS_TAG]-(picture)
            
            CREATE (tag2:Tag {
                id: 'tag2',
                label: 'Tag 2',
                textColor: '#000000',
                color: '#ffffff'
            })<-[:HAS_TAG]-(picture)
            
            CREATE (tag3:Tag {
                id: 'tag3',
                label: 'Tag 3',
                textColor: '#000000',
                color: '#ffffff'
            })
        """.trimIndent()

    }
}
