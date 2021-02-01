package nl.juraji.albums.configuration

import org.liquigraph.core.api.Liquigraph
import org.liquigraph.core.configuration.ConfigurationBuilder
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class LiquigraphConfiguration(
    private val neo4jProperties: Neo4jProperties,
) : InitializingBean {

    override fun afterPropertiesSet() {
        val configuration: org.liquigraph.core.configuration.Configuration = ConfigurationBuilder()
            .withMasterChangelogLocation("db/liquigraph/changelog.xml")
            .withUri("jdbc:neo4j:${neo4jProperties.uri}/")
            .withUsername(neo4jProperties.authentication.username)
            .withPassword(neo4jProperties.authentication.password)
            .withRunMode()
            .build()

        val liquigraph = Liquigraph()
        liquigraph.runMigrations(configuration)
    }
}
