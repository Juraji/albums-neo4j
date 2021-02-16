package nl.juraji.albums.util.validators

import nl.juraji.albums.configurations.TestApiConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.Validation
import javax.validation.Validator

internal class ValidFileSystemPathValidatorTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

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
