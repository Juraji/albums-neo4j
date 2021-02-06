package nl.juraji.albums.domain.relationships

import nl.juraji.albums.configurations.TestNeo4jFixtureConfiguration
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier
import java.time.LocalDateTime

@DataNeo4jTest
@Import(TestNeo4jFixtureConfiguration::class, DuplicatedByRepository::class)
internal class DuplicatedByRepositoryTest {
    @Autowired
    private lateinit var duplicatedByRepository: DuplicatedByRepository

    @Test
    internal fun `should find duplicates in both directions`() {
        val p2 = DuplicatedBy(
            matchedOn = LocalDateTime.parse("2020-05-16T11:00:50"),
            similarity = 0.86,
            picture = Picture(
                id = "p2",
                location = "F:\\Desktop\\TESTMAP\\78Kng.jpg",
                name = "78Kng.jpg",
                fileSize = 916566,
                fileType = FileType.BMP,
                lastModified = LocalDateTime.parse("2020-05-16T11:00:50"),
            )
        )

        StepVerifier.create(duplicatedByRepository.findByPictureId("p1"))
            .expectNext(p2)
            .verifyComplete()
    }
}
