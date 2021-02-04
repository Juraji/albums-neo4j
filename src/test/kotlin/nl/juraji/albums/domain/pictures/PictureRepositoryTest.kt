package nl.juraji.albums.domain.pictures

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.neo4j.core.Neo4jTemplate
import reactor.test.StepVerifier

@DataNeo4jTest
class PictureRepositoryTest {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Autowired
    private lateinit var neo4jTemplate: Neo4jTemplate

    @Test
    internal fun `should remove TAGGED_BY relationship for given picture id and tag id`() {
        StepVerifier.create(pictureRepository.removeTaggedByTag("p1", "t1"))
            .verifyComplete()

        val taggedByRelCount = neo4jTemplate.count(
            // language=cypher
            """
                MATCH (p:Picture)-[rel:TAGGED_BY]->(t:Tag)
                  WHERE p.id = $ pictureId AND t.id = $ tagId
                RETURN count(rel)
            """.trimIndent(),
            mapOf("pictureId" to "p1", "tagId" to "t1")
        )

        Assertions.assertEquals(0, taggedByRelCount)
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship for picture id and target id`() {
        StepVerifier.create(pictureRepository.removeDuplicatedBy("p1", "p2"))
            .verifyComplete()

        val taggedByRelCount = neo4jTemplate.count(
            // language=cypher
            """
                MATCH (p1:Picture)-[rel:DUPLICATED_BY]->(p2:Picture)
                  WHERE p1.id = $ pictureId AND p2.id = $ targetId
                RETURN count(rel)
            """.trimIndent(),
            mapOf("pictureId" to "p1", "targetId" to "p2")
        )

        Assertions.assertEquals(0, taggedByRelCount)
    }

    @TestConfiguration
    class TestHarnessConfig {

        @Bean
        fun driver(): Driver = GraphDatabase.driver(
            neo4j().boltURI(),
            AuthTokens.basic("neo4j", "")
        )

        @Bean
        fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(
                // language=cypher
                """
                   CREATE (p1:Picture {
                     fileSize: 64367,
                     fileType: 'JPEG',
                     id: 'p1',
                     lastModified: '2020-05-16T10:17:50',
                     location: 'F:\Desktop\TESTMAP\DA37o272cCU.jpg',
                     name: 'DA37o272cCU.jpg'
                   })

                   CREATE (p2:Picture {
                     fileSize: 916566,
                     fileType: 'BMP',
                     id: 'p2',
                     lastModified: '2020-05-16T11:00:50',
                     location: 'F:\Desktop\TESTMAP\78Kng.jpg',
                     name: '78Kng.jpg'
                   })

                   CREATE (t1:Tag {
                     id: 't1',
                     label: 'My Tag',
                     color: '#00ff00'
                   })
                   
                   WITH p1,p2,t1
                   CREATE (p1)-[:TAGGED_BY]->(t1)
                   CREATE (p1)-[:DUPLICATED_BY]->(p2)
                """.trimIndent()
            )
            .build()
    }
}
