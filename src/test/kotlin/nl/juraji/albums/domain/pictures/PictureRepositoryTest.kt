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
class PictureRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Test
    internal fun `should add TAGGED_BY relationship on picture to tag`() {
        StepVerifier.create(pictureRepository.addTag("p4", "t1"))
            .verifyComplete()

        assertYieldsOneRecord("MATCH (:Picture {id: 'p4'})-[rel:TAGGED_BY]->(:Tag {id: 't1'}) RETURN rel")
    }

    @Test
    internal fun `should remove TAGGED_BY relationship from picture and tag`() {
        StepVerifier.create(pictureRepository.removeTag("p1", "t1"))
            .verifyComplete()

        assertYieldsNoRecords("MATCH (:Picture {id: 'p1'})-[rel:TAGGED_BY]->(:Tag {id: 't1'}) RETURN rel")
    }
}
