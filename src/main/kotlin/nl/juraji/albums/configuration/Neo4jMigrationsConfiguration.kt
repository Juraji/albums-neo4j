package nl.juraji.albums.configuration

import ac.simons.neo4j.migrations.core.Migrations
import ac.simons.neo4j.migrations.core.MigrationsConfig
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class Neo4jMigrationsConfiguration(
    private val neo4jProperties: Neo4jProperties,
) : InitializingBean {

    override fun afterPropertiesSet() {
        applyMigrations()
    }

    fun applyMigrations() {
        val config = MigrationsConfig.builder()
            .withLocationsToScan(
                "classpath:neo4j/migrations",
            )
            .build()

        val driver = GraphDatabase.driver(
            neo4jProperties.uri,
            AuthTokens.basic(
                neo4jProperties.authentication.username,
                neo4jProperties.authentication.password
            )
        )

        Migrations(config, driver).apply()
    }
}
