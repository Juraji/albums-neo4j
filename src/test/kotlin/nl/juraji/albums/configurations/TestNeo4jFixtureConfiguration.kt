package nl.juraji.albums.configurations

import nl.juraji.albums.util.LoggerCompanion
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD

@TestConfiguration
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
internal class TestNeo4jFixtureConfiguration {

    @Bean
    fun driver(): Driver = GraphDatabase.driver(
        neo4j().boltURI(),
        AuthTokens.basic("neo4j", "")
    )

    @Bean
    fun neo4j(): Neo4j {
        val graph = this::class.java.getResource("/neo4j-test-harness-initial-graph.cyp").readText()

        val server = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(graph)
            .build()

        logger.debug("Started Neo4j test harness on: ${server.boltURI()}")
        return server
    }

    companion object : LoggerCompanion(TestNeo4jFixtureConfiguration::class)
}
