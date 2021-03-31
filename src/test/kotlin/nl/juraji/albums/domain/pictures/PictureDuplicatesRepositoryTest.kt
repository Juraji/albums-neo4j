package nl.juraji.albums.domain.pictures

import nl.juraji.albums.util.BaseTestHarnessConfig
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier

@DataNeo4jTest
@Import(PictureDuplicatesRepository::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class PictureDuplicatesRepositoryTest {

    @Autowired
    private lateinit var duplicatesRepository: PictureDuplicatesRepository

    @Test
    @Order(1)
    fun `should find all`() {
        val result = duplicatesRepository.findAll()

        StepVerifier.create(result)
            .expectNextMatches { it.similarity == 0.87 && it.source.id == "picture1" && it.target.id == "picture2" }
            .expectNextMatches { it.similarity == 0.93 && it.source.id == "picture3" && it.target.id == "picture4" }
            .verifyComplete()
    }

    @Test
    @Order(2)
    fun `should set as duplicate`() {
        val result = duplicatesRepository.setAsDuplicate("picture2", "picture3", 0.68)

        StepVerifier.create(result)
            .expectNextMatches { it.similarity == 0.68 && it.target.id == "picture3" }
            .verifyComplete()
    }

    @Test
    @Order(3)
    fun `should remove as duplicate`() {
        val result = duplicatesRepository.removeAsDuplicate("picture3", "picture4")

        StepVerifier.create(result)
            .verifyComplete()
    }

    @TestConfiguration
    class TestHarnessConfig : BaseTestHarnessConfig() {
        // language=Cypher
        override fun withFixtureQuery(): String = """
            CREATE (picture1:Picture {
                id: 'picture1',
                name: 'Picture 1',
                type: 'JPEG',
                width: 651,
                height: 640,
                fileSize: 191660,
                addedOn: localdatetime('2021-03-25T19:49:00'),
                thumbnailLocation: '/thumbnails/picture1.jpg',
                pictureLocation: '/pictures/picture1.jpg'
            })
            
            CREATE (picture2:Picture {
                id: 'picture2',
                name: 'Picture 2',
                type: 'TIFF',
                width: 288,
                height: 976,
                fileSize: 921524,
                addedOn: localdatetime('2021-03-25T19:49:00'),
                thumbnailLocation: '/thumbnails/picture2.jpg',
                pictureLocation: '/pictures/picture2.tiff'
            })<-[:DUPLICATED_BY {similarity: 0.87}]-(picture1)
            
            CREATE (picture3:Picture {
                id: 'picture3',
                name: 'Picture 3',
                type: 'BMP',
                width: 288,
                height: 976,
                fileSize: 921524,
                addedOn: localdatetime('2021-03-25T19:49:00'),
                thumbnailLocation: '/thumbnails/picture3.jpg',
                pictureLocation: '/pictures/picture3.bmp'
            })
            
            CREATE (picture4:Picture {
                id: 'picture4',
                name: 'Picture 4',
                type: 'BMP',
                width: 288,
                height: 976,
                fileSize: 921524,
                addedOn: localdatetime('2021-03-25T19:49:00'),
                thumbnailLocation: '/thumbnails/picture4.jpg',
                pictureLocation: '/pictures/picture4.bmp'
            })<-[:DUPLICATED_BY {similarity: 0.93}]-(picture3)
        """.trimIndent()

    }
}
