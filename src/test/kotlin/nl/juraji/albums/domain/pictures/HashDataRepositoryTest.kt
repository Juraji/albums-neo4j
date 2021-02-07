package nl.juraji.albums.domain.pictures

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class)
class HashDataRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var hashDataRepository: HashDataRepository

    @Test
    internal fun `should find by picture id`() {
        StepVerifier.create(hashDataRepository.findByPictureId("p1"))
            .expectNext(HashData(id = "hd1", hash = "//3333///ffff//9Aw=="))
            .verifyComplete()
    }

    @Test
    internal fun `should set picture hash data`() {
        StepVerifier.create(hashDataRepository.setPictureHashData("p4", "hd2"))
            .verifyComplete()

        assertYieldsOneRecord("MATCH (:Picture {id: 'p4'})-[rel:DESCRIBED_BY]->(:HashData {id: 'hd2'}) RETURN rel")
    }
}
