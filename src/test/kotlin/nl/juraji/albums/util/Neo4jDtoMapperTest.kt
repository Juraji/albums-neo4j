package nl.juraji.albums.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.neo4j.driver.Driver
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

@SpringBootTest
internal class Neo4jDtoMapperTest {
    private val mapper = Neo4jDtoMapper(TestNodeDtoObj::class)

    @Test
    internal fun `should convert record to dto class`(@Autowired driver: Driver) {
        val id = "complete"
        val record1 = driver.session().use { session ->
            session.run("MATCH (n:Dto {identifier: $ id}) RETURN n", mapOf("id" to id)).single()
        }

        val dto = mapper.recordToDto(record1)

        assertEquals(id, dto.identifier)
        assertEquals(false, dto.boolean)
        assertEquals(1.26, dto.double)
        assertEquals(BigDecimal.valueOf(1.12), dto.bigDecimal)
        assertEquals(313, dto.int)
        assertEquals(LocalDate.of(2021, Month.JANUARY, 8), dto.localDate)
        assertEquals(LocalDateTime.of(2021, Month.JANUARY, 8, 21, 59, 36, 365000000), dto.localDateTime)
        assertEquals(LocalTime.of(21, 59, 36, 365000000), dto.localTime)
        assertEquals(1351556444L, dto.long)
    }

    @Test
    internal fun `should support nullable values convert record to dto class`(@Autowired driver: Driver) {
        val id = "partial"
        val record1 = driver.session().use { session ->
            session.run("MATCH (n:Dto {identifier: $ id}) RETURN n", mapOf("id" to id)).single()
        }

        val dto = mapper.recordToDto(record1)

        assertEquals(id, dto.identifier)
        assertEquals(false, dto.boolean)
        assertEquals(1.26, dto.double)
        assertEquals(BigDecimal.valueOf(1.12), dto.bigDecimal)
        assertEquals(313, dto.int)
        assertEquals(LocalDate.of(2021, Month.JANUARY, 8), dto.localDate)
        assertNull(dto.localTime)
        assertNull(dto.localDateTime)
        assertNull(dto.long)
    }

    @Test
    internal fun `should throw when required dto properties are missing`(@Autowired driver: Driver) {
        val id = "missingRequired"
        val record1 = driver.session().use { session ->
            session.run("MATCH (n:Dto {identifier: $ id}) RETURN n", mapOf("id" to id)).single()
        }

        assertThrows(Neo4jDtoMapper.RequiredPropertyNotFoundException::class.java) {
            mapper.recordToDto(record1)
        }
    }

    @TestConfiguration
    class TestHarnessConfig {

        @Bean
        fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer()
            .withFixture(
                """
                CREATE (d1:Dto {
                    identifier: 'complete',
                    boolean: false,
                    double: 1.26,
                    bigDecimal: 1.12,
                    int: 313,
                    localDate: date('2021-01-08'),
                    localDateTime: localdatetime('2021-01-08T21:59:36.365'),
                    localTime: localtime('21:59:36.365'),
                    long: 1351556444
                })
                CREATE (d2:Dto {
                    identifier: 'partial',
                    boolean: false,
                    double: 1.26,
                    bigDecimal: 1.12,
                    int: 313,
                    localDate: date('2021-01-08')
                })
                CREATE (:Dto {
                    identifier: 'missingRequired',
                    bigDecimal: 1.12,
                    int: 313,
                    localDate: date('2021-01-08')
                })
            """.trimIndent()
            )
            .build()
    }

    data class TestNodeDtoObj(
        val identifier: String,
        val boolean: Boolean,
        val double: Double,
        val bigDecimal: BigDecimal,
        val int: Int,
        val localDate: LocalDate,
        val localDateTime: LocalDateTime?,
        val localTime: LocalTime?,
        val long: Long?,
    )
}
