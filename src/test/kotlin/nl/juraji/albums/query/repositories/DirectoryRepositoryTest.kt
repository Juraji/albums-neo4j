package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Directory
import org.junit.jupiter.api.Test
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import reactor.test.StepVerifier

@SpringBootTest
internal class DirectoryRepositoryTest {

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Test
    internal fun `should list all directories`() {
        val result = directoryRepository.findAll()

        StepVerifier.create(result)
            .expectNext(Directory(location = "/directory1"))
            .expectNext(Directory(location = "/directory2"))
            .expectNext(Directory(location = "/directory3"))
            .verifyComplete()
    }

    @TestConfiguration
    class TestHarnessConfig {

        @Bean
        fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(
                """
                        CREATE (d1:Directory {
                            location: '/directory1'
                        })
                        CREATE (d2:Directory {
                            location: '/directory2'
                        })
                        CREATE (d3:Directory {
                            location: '/directory3'
                        })
                    """.trimIndent()
            )
            .build()
    }
}
