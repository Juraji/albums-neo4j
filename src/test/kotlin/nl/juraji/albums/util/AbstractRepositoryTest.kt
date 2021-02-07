package nl.juraji.albums.util

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.data.neo4j.core.fetchAs

abstract class AbstractRepositoryTest {

    @Autowired
    protected lateinit var neo4jClient: Neo4jClient

    protected fun assertCount(expected: Long, @Language("CYPHER") query: String) {
        val actual = neo4jClient.query(query).fetchAs<Long>().first()
        Assertions.assertEquals(expected, actual, query)
    }
}
