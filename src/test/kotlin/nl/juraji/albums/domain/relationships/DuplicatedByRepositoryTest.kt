package nl.juraji.albums.domain.relationships

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class, DuplicatedByRepository::class)
internal class DuplicatedByRepositoryTest : AbstractRepositoryTest() {
    @Autowired
    private lateinit var duplicatedByRepository: DuplicatedByRepository

    @Test
    internal fun `should find duplicates in both directions`() {
        StepVerifier.create(duplicatedByRepository.findByPictureId("p1"))
            .expectNextCount(2)
            .verifyComplete()
    }

    @Test
    internal fun `should find all distinct duplicated by`() {
        val recorder = mutableListOf<DuplicatedByWithSource>()

        StepVerifier.create(duplicatedByRepository.findAllDistinctDuplicatedBy())
            .expectNextCount(3)
            .verifyComplete()

        recorder.forEach { println(it) }
    }
}
