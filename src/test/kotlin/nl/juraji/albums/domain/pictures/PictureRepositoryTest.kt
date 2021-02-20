package nl.juraji.albums.domain.pictures

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.util.AbstractRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import reactor.test.StepVerifier

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class)
class PictureRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Test
    internal fun `should find image location by id`() {
        StepVerifier.create(pictureRepository.findImageLocationById("p1"))
            .expectNext("F:\\Desktop\\TestMap\\DA37o272cCU.jpg")
            .verifyComplete()
    }

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

    @Test
    @DirtiesContext
    internal fun `should delete tree, including duplicates and hash data`() {
        StepVerifier.create(pictureRepository.deleteTreeById("p1"))
            .verifyComplete()

        assertYieldsNoRecords("MATCH (n:Picture {id: 'p1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:HashData {id: 'hd1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Duplicate {id: 'dup1'}) RETURN n")
        assertYieldsNoRecords("MATCH (n:Duplicate {id: 'dup2'}) RETURN n")

        assertYieldsOneRecord("MATCH (n:Directory {id: 'd1'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Directory {id: 'd2'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Directory {id: 'd3'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Picture {id: 'p2'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Picture {id: 'p3'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Picture {id: 'p4'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Tag {id: 't1'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Tag {id: 't2'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:HashData {id: 'hd2'}) RETURN n")
        assertYieldsOneRecord("MATCH (n:Duplicate {id: 'dup3'}) RETURN n")
    }
}
