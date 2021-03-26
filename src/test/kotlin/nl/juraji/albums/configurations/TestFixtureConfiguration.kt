package nl.juraji.albums.configurations

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.FixtureConfigs
import com.marcellogalhardo.fixture.next
import nl.juraji.albums.domain.pictures.FileType
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
            longRange = LONG_RANGE
        )
    )

    fun registerDomainFixtures(): Fixture.() -> Unit = {
        register(Instant::class) { Instant.ofEpochMilli(nextLong(INSTANT_RANGE)) }
        register(LocalDateTime::class) { LocalDateTime.ofInstant(next(), ZoneId.systemDefault()) }
        register(FileType::class) {
            val values = FileType.values()
            values[nextInt(values.size)]
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

        private val LONG_RANGE: LongRange = 1L..100000L
        private val INSTANT_RANGE: LongRange = 1577833200000..1609455600000
    }
}

fun Fixture.nextHexOctet(): String = Integer.toHexString(nextInt(256)).padStart(2, '0')

fun Fixture.nextHexColor(): String = "#" + nextHexOctet() + nextHexOctet() + nextHexOctet()
