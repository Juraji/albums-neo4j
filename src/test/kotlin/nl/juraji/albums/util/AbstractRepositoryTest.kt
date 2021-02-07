package nl.juraji.albums.util

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.Neo4jClient

abstract class AbstractRepositoryTest {

    @Autowired
    protected lateinit var neo4jClient: Neo4jClient

    protected fun assertYieldsOneRecord(@Language("CYPHER") query: String) {
        val result = neo4jClient.query(query).fetch().all()
        assertEquals(1, result.size, "Expected 1 record for query: ${query.trimIndent()}\nBut got: $result")
    }

    protected fun assertYieldsNoRecords(@Language("CYPHER") query: String) {
        val result = neo4jClient.query(query).fetch().all()
        assertEquals(0, result.size, "Expected no records for query: ${query.trimIndent()}\nBut got: $result")
    }
}
