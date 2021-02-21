package nl.juraji.albums.configurations

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.FixtureConfigs
import com.marcellogalhardo.fixture.next
import com.marcellogalhardo.fixture.register
import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.api.dto.UpdateTagDto
import nl.juraji.albums.domain.directories.Directory
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.tags.Tag
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@TestConfiguration
class TestFixtureConfiguration {

    @Bean
    fun fixture(): Fixture = Fixture(
        apply = this.registerDomainFixtures(),
        configs = FixtureConfigs(
            longRange = 1L..100000L
        )
    )

    fun registerDomainFixtures(): Fixture.() -> Unit = {
        register { Instant.ofEpochMilli(nextLong(INSTANT_RANGE)) }
        register { LocalDateTime.ofInstant(next(), ZoneId.systemDefault()) }
        register { Tag(id = nextString(), label = nextString(), color = nextHexColor(), textColor = nextHexColor()) }
        register { NewTagDto(label = nextString(), color = nextHexColor(), textColor = nextHexColor()) }
        register {
            UpdateTagDto(
                id = nextString(),
                label = nextString(),
                color = nextHexColor(),
                textColor = nextHexColor()
            )
        }
        register {
            val fts = FileType.values()
            fts[nextInt(fts.size)]
        }
        register {
            Directory(
                id = nextString(),
                location = "/some/location",
                name = nextString()
            )
        }
        register(BasicFileAttributes::class) {
            object : BasicFileAttributes {
                override fun lastModifiedTime(): FileTime = FileTime.from(next())
                override fun lastAccessTime(): FileTime = FileTime.from(next())
                override fun creationTime(): FileTime = FileTime.from(next())
                override fun isRegularFile(): Boolean = nextBoolean()
                override fun isDirectory(): Boolean = nextBoolean()
                override fun isSymbolicLink(): Boolean = nextBoolean()
                override fun isOther(): Boolean = nextBoolean()
                override fun size(): Long = nextLong()
                override fun fileKey(): Any = nextString()
            }
        }
    }

    companion object {
        fun testFixture(): Fixture = TestFixtureConfiguration().fixture()

        private val INSTANT_RANGE: LongRange = 1577833200000..1609455600000
    }
}

fun Fixture.nextHexOctet(): String = Integer.toHexString(nextInt(256)).padStart(2, '0')

fun Fixture.nextHexColor(): String = "#" + nextHexOctet() + nextHexOctet() + nextHexOctet()
