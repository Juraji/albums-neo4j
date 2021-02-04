package nl.juraji.albums.util.validators

import nl.juraji.albums.configurations.TestApiConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@SpringBootTest
@Import(TestApiConfiguration::class)
internal class ValidFileSystemPathValidatorTest {

    @Autowired
    private lateinit var validator: LocalValidatorFactoryBean

    @Test
    internal fun `should succeed constraint when path is valid`() {
        val violations = validator.validate(ValidatedModel("/some/path"))
        assertEquals(0, violations.size)
    }

    @Test
    internal fun `should fail constraint when path is not valid`() {
        val violations = validator.validate(ValidatedModel("|some invalid path?"))
        assertEquals(1, violations.size)
    }

    internal data class ValidatedModel(
        @field:ValidFileSystemPath
        val path: String
    )
}
