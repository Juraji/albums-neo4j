package nl.juraji.albums.domain.pictures

import org.intellij.lang.annotations.Language
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
import java.time.LocalDateTime

@DataNeo4jTest
class PictureRepositoryTest {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Autowired
    private lateinit var neo4jTemplate: Neo4jTemplate

    @Test
    internal fun `should add TAGGED_BY relationship on picture to tag`() {
        StepVerifier.create(pictureRepository.addTag("p2", "t1"))
            .verifyComplete()

        assertCount(1, "MATCH (:Picture {id: 'p2'})-[rel:TAGGED_BY]->(:Tag {id: 't1'}) RETURN count(rel)")
    }

    @Test
    internal fun `should remove TAGGED_BY relationship from picture and tag`() {
        StepVerifier.create(pictureRepository.removeTag("p1", "t1"))
            .verifyComplete()

        assertCount(0, "MATCH (:Picture {id: 'p1'})-[rel:TAGGED_BY]->(:Tag {id: 't1'}) RETURN count(rel)")
    }

    @Test
    internal fun `should add DUPLICATED_BY relationship on source and target pictures`() {
        StepVerifier.create(pictureRepository.addDuplicatedBy("p1", "p3", 0.98, LocalDateTime.now()))
            .verifyComplete()

        assertCount(1, "MATCH (:Picture {id: 'p1'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p3'}) RETURN count(rel)")
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship from source and target pictures`() {
        StepVerifier.create(pictureRepository.removeDuplicatedBy("p1", "p2"))
            .verifyComplete()

        assertCount(0, "MATCH (:Picture {id: 'p1'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p2'}) RETURN count(rel)")
    }

    private fun assertCount(expected: Long, @Language("CYPHER") query: String) {
        val actual = neo4jTemplate.count(query)
        Assertions.assertEquals(expected, actual)
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

                   CREATE (p3:Picture {
                     fileSize: 48863,
                     fileType: 'TIFF',
                     id: 'p3',
                     lastModified: '2020-05-16T11:00:50',
                     location: 'F:\Desktop\TESTMAP\79th9.jpg',
                     name: '79th9.jpg'
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
