package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Picture
import org.junit.jupiter.api.Test
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
internal class PictureRepositoryTest {

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Test
    internal fun `should create picture`() {
        val p1 = Picture(
            location = "/pictures/picture-new.jpg",
            name = "Picture New",
            fileSize = 146 * 1024,
            lastModified = LocalDateTime.now(),
        )

        val result = pictureRepository.save(p1)

        StepVerifier.create(result)
            .expectNext(p1)
            .verifyComplete()
    }

    @Test
    internal fun `should list all pictures`() {
        val result = pictureRepository.findAll()

        StepVerifier.create(result)
            .expectNextMatches { it.location == "/pictures/picture1.jpg" }
            .expectNextMatches { it.location == "/pictures/picture2.jpg" }
            .expectNextMatches { it.location == "/pictures/picture3.jpg" }
            .verifyComplete()
    }

    @Test
    internal fun `should get picture by location`() {
        val location = "/pictures/picture2.jpg"
        val result = pictureRepository.findByLocation(location)

        StepVerifier.create(result)
            .expectNextMatches { it.location == location }
            .verifyComplete()
    }

    @Test
    internal fun `should list pictures by tag`() {
        val label = "Tag 1"
        val result = pictureRepository.findByTag(label)

        StepVerifier.create(result)
            .expectNextMatches { it.location == "/pictures/picture1.jpg" }
            .expectNextMatches { it.location == "/pictures/picture2.jpg" }
            .verifyComplete()
    }

    @Test
    internal fun `should list duplicates by location`() {
        val location = "/pictures/picture1.jpg"
        val result = pictureRepository.findDuplicatedByForLocation(location)

        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    internal fun `should list duplicates by location in reverse`() {
        val location = "/pictures/picture2.jpg"
        val result = pictureRepository.findDuplicatedByForLocation(location)

        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    internal fun `should add TAGGED_BY relation`() {
        val pLocation = "/pictures/picture1.jpg"
        val tLabel = "Tag 2"

        val result = pictureRepository.addTaggedByRelation(pLocation, tLabel)

        StepVerifier.create(result)
            .expectNextMatches { it.counters().relationshipsCreated() == 1 }
            .verifyComplete()
    }

    @Test
    internal fun `should remove TAGGED_BY relation`() {
        val pLocation = "/pictures/picture1.jpg"
        val tLabel = "Tag 1"

        val result = pictureRepository.removeTaggedByRelation(pLocation, tLabel)

        StepVerifier.create(result)
            .expectNextMatches { it.counters().relationshipsDeleted() == 1 }
            .verifyComplete()
    }

    @Test
    internal fun `should add DUPLICATED_BY relation`() {
        val pLocation1 = "/pictures/picture1.jpg"
        val pLocation2 = "/pictures/picture3.jpg"
        val time = LocalDateTime.now()
        val similarity = 0.82

        val result = pictureRepository.addDuplicatedByRelation(pLocation1, pLocation2, time, similarity)

        StepVerifier.create(result)
            .expectNextMatches { it.counters().relationshipsCreated() == 1 }
            .verifyComplete()
    }

    @Test
    internal fun `should remove DUPLICATED_BY relation`() {
        val pLocation1 = "/pictures/picture1.jpg"
        val pLocation2 = "/pictures/picture3.jpg"

        val result = pictureRepository.removeDuplicatedByRelation(pLocation1, pLocation2)

        StepVerifier.create(result)
            .expectNextMatches { it.counters().relationshipsDeleted() == 1 }
            .verifyComplete()
    }

    @TestConfiguration
    class TestHarnessConfig {

        @Bean
        fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(
                """
                        WITH localdatetime('2021-01-08T21:59:36.365') as testTime
    
                        CREATE (p1:Picture {
                            location: '/pictures/picture1.jpg',
                            name: 'Picture 1',
                            fileSize: 68 * 1024,
                            lastModified: testTime
                        })
                        CREATE (p2:Picture {
                            location: '/pictures/picture2.jpg',
                            name: 'Picture 2',
                            fileSize: 189 * 1024,
                            lastModified: testTime
                        })
                        CREATE (p3:Picture {
                            location: '/pictures/picture3.jpg',
                            name: 'Picture 3',
                            fileSize: 457 * 1024,
                            lastModified: testTime
                        })
                        CREATE (t1:Tag {label: 'Tag 1', color: '#ff0000'})
                        CREATE (t2:Tag {label: 'Tag 2', color: '#00ff00'})
    
                        WITH p1,p2,p3,t1,testTime
                        MERGE (p1)-[:TAGGED_BY]->(t1)
                        MERGE (p2)-[:TAGGED_BY]->(t1)
                        MERGE (p1)-[:DUPLICATED_BY {matchedAt: testTime, similarity: 89.0}]-(p2)
                    """.trimIndent()
            )
            .build()
    }
}
