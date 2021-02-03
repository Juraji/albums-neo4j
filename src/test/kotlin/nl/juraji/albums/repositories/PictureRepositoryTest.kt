package nl.juraji.albums.repositories

import org.junit.jupiter.api.Assertions.assertEquals
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

    private val pictureId = "cfd68169-5ba1-4f07-94a6-97bfd5bacb31"
    private val tagId = "d8b064d0-64e5-429b-8d11-aa2e3f35d31b"

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Autowired
    private lateinit var neo4jTemplate: Neo4jTemplate

    @Test
    internal fun `should remove TAGGED_BY relationship for given picture id and tag id`() {
        StepVerifier.create(pictureRepository.removePictureTaggedByTag(pictureId, tagId))
            .verifyComplete()

        val taggedByRelCount = neo4jTemplate.count(
            // language=cypher
            """
                MATCH (p:Picture)-[rel:TAGGED_BY]->(t:Tag)
                  WHERE p.id = $ pictureId AND t.id = $ tagId
                RETURN count(rel)
            """.trimIndent(),
            mapOf("pictureId" to pictureId, "tagId" to tagId)
        )

        assertEquals(0, taggedByRelCount)
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
                   CREATE (p:Picture {
                     fileSize: 64367,
                     fileType: 'JPEG',
                     id: 'cfd68169-5ba1-4f07-94a6-97bfd5bacb31',
                     lastModified: '2020-05-16T10:17:50',
                     location: 'F:\Desktop\TESTMAP\duplicates\DA37o272cCU.jpg',
                     name: 'DA37o272cCU.jpg'
                   })

                   CREATE (t:Tag {
                     id: 'd8b064d0-64e5-429b-8d11-aa2e3f35d31b',
                     label: 'My Tag',
                     color: '#00ff00'
                   })
                   
                   WITH p,t
                   CREATE (p)-[:TAGGED_BY]->(t)
                """.trimIndent()
            )
            .build()
    }
}
