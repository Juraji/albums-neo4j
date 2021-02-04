package nl.juraji.albums.domain.pictures

import nl.juraji.albums.domain.directories.DirectoryRepository
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

@DataNeo4jTest
class DirectoryRepositoryTest {

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Autowired
    private lateinit var neo4jTemplate: Neo4jTemplate

    @Test
    internal fun `should add CONTAINS relationship on directory to picture`() {
        StepVerifier.create(directoryRepository.addPicture("d1", "p1"))
            .verifyComplete()

        assertCount(1, "MATCH (:Directory {id: 'd1'})-[rel:CONTAINS]->(:Picture {id: 'p1'}) RETURN count(rel)")
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
                    CREATE(:Directory {
                      id: 'd1',
                      location: 'F:\Desktop\TESTMAP'
                    })
                    
                    CREATE (:Picture {
                      fileSize: 64367,
                      fileType: 'JPEG',
                      id: 'p1',
                      lastModified: '2020-05-16T10:17:50',
                      location: 'F:\Desktop\TESTMAP\DA37o272cCU.jpg',
                      name: 'DA37o272cCU.jpg'
                    })
                """.trimIndent()
            )
            .build()
    }
}
