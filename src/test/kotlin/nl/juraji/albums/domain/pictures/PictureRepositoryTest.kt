package nl.juraji.albums.domain.pictures

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier
import java.time.LocalDateTime

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class)
class PictureRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Test
    internal fun `should add DUPLICATED_BY relationship on source and target pictures`() {
        val matchedOn = LocalDateTime.parse("2020-03-12T14:34:47")
        val similarity = 0.98

        StepVerifier.create(pictureRepository.addDuplicatedBy("p3", "p4", similarity, matchedOn))
            .verifyComplete()

        assertYieldsOneRecord(
            """
              MATCH (:Picture {id: 'p3'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p4'})
                WHERE rel.similarity = 0.98 AND rel.matchedOn = localdatetime('2020-03-12T14:34:47')
              RETURN rel
            """
        )
    }

    @Test
    internal fun `should overwrite existing DUPLICATED_BY relationship`() {
        val matchedOn = LocalDateTime.parse("2020-03-12T14:34:47")
        val similarity = 0.98

        StepVerifier.create(pictureRepository.addDuplicatedBy("p2", "p1", similarity, matchedOn))
            .verifyComplete()

        assertYieldsNoRecords("MATCH (:Picture {id: 'p1'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p2'}) RETURN rel")
        assertYieldsOneRecord("MATCH (:Picture {id: 'p2'})-[rel:DUPLICATED_BY]->(:Picture {id: 'p1'}) RETURN rel")
    }

    @Test
    internal fun `should remove DUPLICATED_BY relationship from source and target pictures in both directions`() {
        StepVerifier.create(pictureRepository.removeDuplicatedBy("p1", "p2"))
            .verifyComplete()

        assertYieldsNoRecords("MATCH (:Picture {id: 'p1'})-[rel:DUPLICATED_BY]-(:Picture {id: 'p2'}) RETURN rel")
    }
}
