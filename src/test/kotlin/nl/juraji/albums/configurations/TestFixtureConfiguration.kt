package nl.juraji.albums.configurations

import com.marcellogalhardo.fixture.Fixture
import nl.juraji.albums.model.Directory
import nl.juraji.albums.model.FileType
import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.Tag
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime

@TestConfiguration
class TestFixtureConfiguration {

    @Bean
    fun fixture(): Fixture = Fixture(apply = this.registerDomainFixtures())

    fun registerDomainFixtures(): Fixture.() -> Unit = {
        register(Directory::class) {
            Directory(
                id = it.nextString(),
                location = it.nextString()
            )
        }

        register(Picture::class) {
            Picture(
                id = it.nextString(),
                location = it.nextString(),
                name = it.nextString(),
                fileSize = it.nextLong(),
                fileType = FileType.JPG,
                lastModified = LocalDateTime.now()
            )
        }

        register(Tag::class) {
            Tag(
                id = it.nextString(),
                label = it.nextString(),
                color = "#000000"
            )
        }
    }

    companion object {
        fun testFixture(): Fixture = TestFixtureConfiguration().fixture()
    }
}
