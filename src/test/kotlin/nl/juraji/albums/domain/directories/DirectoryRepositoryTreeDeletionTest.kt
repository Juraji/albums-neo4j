package nl.juraji.albums.domain.directories

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import reactor.test.StepVerifier

@DataNeo4jTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestNeo4jFixtureConfiguration::class)
class DirectoryRepositoryTreeDeletionTest : AbstractRepositoryTest() {
    /**
     * Needed to be a separate test, since it deletes entire structures from the test db,
     * failing any other test in the same context.
     */

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Test
    internal fun `should delete tree, including pictures`() {
        StepVerifier.create(directoryRepository.deleteTreeById("d1"))
            .verifyComplete()

        assertYieldsNoRecords("MATCH (n:Directory {id: 'd1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Directory {id: 'd2'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Picture {id: 'p1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Picture {id: 'p2'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Picture {id: 'p3'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:HashData {id: 'hd1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Duplicate {id: 'dup1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Duplicate {id: 'dup2'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Duplicate {id: 'dup3'}) RETURN n")

        assertYieldsOneRecord("MATCH (n:Directory {id: 'd3'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Picture {id: 'p4'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Tag {id: 't1'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Tag {id: 't2'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:HashData {id: 'hd2'}) RETURN n")
    }
}
