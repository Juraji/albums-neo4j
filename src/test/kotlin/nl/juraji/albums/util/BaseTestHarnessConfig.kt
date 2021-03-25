package nl.juraji.albums.util

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.context.annotation.Bean

abstract class BaseTestHarnessConfig {

    abstract fun withFixtureQuery(): String

    @Bean
    open fun driver(): Driver = GraphDatabase.driver(
        neo4j().boltURI(),
        AuthTokens.basic("neo4j", "")
    )

    @Bean
    open fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
        .withDisabledServer()
        .withFixture(withFixtureQuery())
        .build()

}
