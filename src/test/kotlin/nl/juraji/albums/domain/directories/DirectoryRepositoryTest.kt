package nl.juraji.albums.domain.directories

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import reactor.test.StepVerifier

@DataNeo4jTest
@Transactional(propagation = Propagation.NEVER)
@Import(TestNeo4jFixtureConfiguration::class)
class DirectoryRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var directoryRepository: DirectoryRepository

    @Test
    internal fun `should add CONTAINS relationship on directory to picture`() {
        StepVerifier.create(directoryRepository.addPicture("d2", "p4"))
            .verifyComplete()

        assertCount(1, "MATCH (:Directory {id: 'd2'})-[rel:CONTAINS]->(:Picture {id: 'p4'}) RETURN count(rel)")
    }

    @Test
    internal fun `should add PARENT_OF relationship on directory to directory`() {
        neo4jClient.query("CREATE (d:Directory {id: 'd_child'})").run()

        StepVerifier.create(directoryRepository.addChild("d1", "d_child"))
            .verifyComplete()

        assertCount(1, "MATCH (:Directory {id: 'd1'})-[rel:PARENT_OF]->(:Directory {id: 'd_child'}) RETURN count(rel)")
    }
}
