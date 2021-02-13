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
class DirectoryRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Test
    internal fun `should find roots`() {
        StepVerifier.create(directoryRepository.findRoots())
            .expectNext(Directory(id = "d1", location = "F:\\Desktop", name = "Desktop"))
            .expectNext(Directory(id = "d3", location = "F:\\Desktop\\NotLinkedTestMap", name = "NotLinkedTestMap"))
            .verifyComplete()
    }

    @Test
    internal fun `should add PARENT_OF relationship on directory to directory`() {
        StepVerifier.create(directoryRepository.addChild("d1", "d3"))
            .verifyComplete()

        assertYieldsOneRecord("MATCH (:Directory {id: 'd1'})-[rel:PARENT_OF]->(:Directory {id: 'd3'}) RETURN rel")
    }

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
