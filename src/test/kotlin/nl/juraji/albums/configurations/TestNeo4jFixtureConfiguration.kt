package nl.juraji.albums.configurations

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
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

        return Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(graph)
            .build()
    }
}
