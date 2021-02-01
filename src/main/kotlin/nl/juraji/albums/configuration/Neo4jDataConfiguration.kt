package nl.juraji.albums.configuration

import org.neo4j.driver.Driver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories
import org.springframework.transaction.TransactionManager

@Configuration
@EnableReactiveNeo4jRepositories("nl.juraji.albums.repositories")
class Neo4jDataConfiguration {

    @Bean
    @Primary
    fun reactiveTransactionManager(
        driver: Driver,
        databaseSelectionProvider: ReactiveDatabaseSelectionProvider
    ): TransactionManager = ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider)
}
