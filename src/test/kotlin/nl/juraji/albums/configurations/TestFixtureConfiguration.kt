package nl.juraji.albums.configurations

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.register
import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.api.dto.TagDto
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.tags.Tag
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.time.LocalDateTime

@TestConfiguration
class TestFixtureConfiguration {

    @Bean
    fun fixture(): Fixture = Fixture(apply = this.registerDomainFixtures())

    fun registerDomainFixtures(): Fixture.() -> Unit = {
        register { Instant.ofEpochMilli(nextLong()) }
        register { LocalDateTime.now() }
        register { FileType.UNKNOWN }
        register {
            Picture(
                id = nextString(),
                location = nextString(),
                name = nextString(),
                fileSize = next(),
                fileType = next(),
                lastModified = next(),
            )
        }
        register { Tag(id = nextString(), label = nextString(), color = nextHexColor()) }
        register { NewTagDto(label = nextString(), color = nextHexColor()) }
        register { TagDto(id = nextString(), label = nextString(), color = nextHexColor()) }
    }

    companion object {
        fun testFixture(): Fixture = TestFixtureConfiguration().fixture()
    }
}

fun Fixture.nextHexColor(): String {
    return "#" +
            Integer.toHexString(nextInt(256)).padStart(2, '0') +
            Integer.toHexString(nextInt(256)).padStart(2, '0') +
            Integer.toHexString(nextInt(256)).padStart(2, '0')
}
