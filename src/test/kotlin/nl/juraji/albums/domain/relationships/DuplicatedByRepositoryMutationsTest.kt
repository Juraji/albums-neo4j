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
import java.time.LocalDateTime

@DataNeo4jTest
@Transactional(propagation = Propagation.NEVER)
@Import(TestNeo4jFixtureConfiguration::class, DuplicatedByRepository::class)
internal class DuplicatedByRepositoryMutationsTest : AbstractRepositoryTest() {
    @Autowired
    private lateinit var duplicatedByRepository: DuplicatedByRepository

    @Test
    internal fun `should add DUPLICATED_BY relationship on source and target pictures`() {
        val matchedOn = LocalDateTime.parse("2020-03-12T14:34:47")
        val similarity = 0.98

        StepVerifier.create(duplicatedByRepository.addDuplicatedBy("p3", "p4", similarity, matchedOn))
            .verifyComplete()

        assertCount(
            1,
            """
              MATCH (:Picture {id: 'p3'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p4'})
                WHERE rel.similarity = 0.98 AND rel.matchedOn = '2020-03-12T14:34:47'
              RETURN count(rel)
            """
        )
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship from source and target pictures in both directions`() {
        StepVerifier.create(duplicatedByRepository.removeDuplicatedBy("p1", "p2"))
            .verifyComplete()

        assertCount(0, "MATCH (:Picture {id: 'p1'})-[rel:DUPLICATED_BY]-(:Picture {id: 'p2'}) RETURN count(rel)")
    }
}
