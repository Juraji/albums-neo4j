package nl.juraji.albums.configurations

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.register
import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.api.dto.TagDto
import nl.juraji.albums.model.*
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.time.LocalDateTime

@TestConfiguration
class TestFixtureConfiguration {

    @Bean
    fun fixture(): Fixture = Fixture(apply = this.registerDomainFixtures())

    fun registerDomainFixtures(): Fixture.() -> Unit = {
        fun nextHexColor(): String {
            return "#" +
                    Integer.toHexString(nextInt(256)).padStart(2, '0') +
                    Integer.toHexString(nextInt(256)).padStart(2, '0') +
                    Integer.toHexString(nextInt(256)).padStart(2, '0')
        }

        register { Instant.ofEpochMilli(nextLong()) }

        register(Directory::class) {
            Directory(
                id = nextString(),
                location = nextString()
            )
        }

        register(Picture::class) {
            Picture(
                id = nextString(),
                location = nextString(),
                name = nextString(),
                fileSize = 125363,
                fileType = FileType.UNKNOWN,
                lastModified = LocalDateTime.now()
            )
        }

        register(PictureDescription::class) {
            PictureDescription(
                id = nextString(),
                location = nextString(),
                name = nextString(),
                fileSize = nextLong(),
                fileType = FileType.UNKNOWN,
                lastModified = LocalDateTime.now()
            )
        }

        register(Tag::class) {
            Tag(
                id = nextString(),
                label = nextString(),
                color = nextHexColor()
            )
        }

        register(NewTagDto::class) {
            NewTagDto(
                label = nextString(),
                color = nextHexColor()
            )
        }

        register(TagDto::class) {
            TagDto(
                id = nextString(),
                label = nextString(),
                color = nextHexColor()
            )
        }
    }

    companion object {
        fun testFixture(): Fixture = TestFixtureConfiguration().fixture()
    }
}
