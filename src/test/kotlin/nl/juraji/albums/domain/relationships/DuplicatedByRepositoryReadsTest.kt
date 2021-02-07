package nl.juraji.albums.domain.relationships

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
@Import(TestNeo4jFixtureConfiguration::class, DuplicatedByRepository::class)
internal class DuplicatedByRepositoryReadsTest : AbstractRepositoryTest() {
    @Autowired
    private lateinit var duplicatedByRepository: DuplicatedByRepository

    @Test
    internal fun `should find duplicates in both directions`() {
        val recorder = mutableListOf<DuplicatedBy>()

        StepVerifier.create(duplicatedByRepository.findByPictureId("p1"))
            .recordWith { recorder }
            .expectNextCount(2)
            .verifyComplete()

        recorder.forEach { println(it) }
    }

    @Test
    internal fun `should find all distinct duplicated by`() {
        val recorder = mutableListOf<DuplicatedByWithSource>()

        StepVerifier.create(duplicatedByRepository.findAllDistinctDuplicatedBy())
            .recordWith { recorder }
            .expectNextCount(4)
            .verifyComplete()

        recorder.forEach { println(it) }
    }
}
