package nl.juraji.albums.util

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.Neo4jTemplate

abstract class AbstractRepositoryTest {

    @Autowired
    private lateinit var neo4jTemplate: Neo4jTemplate

    protected fun assertCount(expected: Long, @Language("CYPHER") query: String) {
        val actual = neo4jTemplate.count(query)
        Assertions.assertEquals(expected, actual)
    }
}
