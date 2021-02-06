package nl.juraji.albums.configurations

import nl.juraji.albums.util.LoggerCompanion
import org.neo4j.configuration.GraphDatabaseSettings
import org.neo4j.configuration.SettingImpl
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.graphdb.config.Setting
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestNeo4jFixtureConfiguration {

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
