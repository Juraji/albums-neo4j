package nl.juraji.albums.domain.directories

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class)
class DirectoryRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Test
    internal fun `should add CONTAINS relationship on directory to picture`() {
        StepVerifier.create(directoryRepository.addPicture("d2", "p4"))
            .verifyComplete()

        assertYieldsOneRecord("MATCH (:Directory {id: 'd2'})-[rel:CONTAINS]->(:Picture {id: 'p4'}) RETURN rel")
    }

    @Test
    internal fun `should add PARENT_OF relationship on directory to directory`() {
        StepVerifier.create(directoryRepository.addChild("d1", "d3"))
            .verifyComplete()

        assertYieldsOneRecord("MATCH (:Directory {id: 'd1'})-[rel:PARENT_OF]->(:Directory {id: 'd3'}) RETURN rel")
    }
}
