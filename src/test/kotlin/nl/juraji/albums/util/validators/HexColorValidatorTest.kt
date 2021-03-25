package nl.juraji.albums.util.validators

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import javax.validation.Validation
import javax.validation.Validator

internal class HexColorValidatorTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    private val assertions = listOf(
        HexValidatorTestDto("#a12f6e") to 0,
        HexValidatorTestDto("#a12z6e") to 1,
        HexValidatorTestDto("a12f6e") to 1,
        HexValidatorTestDto("#a12f6e0") to 1,
    )

    @TestFactory
    fun test() = assertions.map { (dto, assertViolationCount) ->
        DynamicTest.dynamicTest(
            "Given hexValue=[${dto.hexValue}], expect $assertViolationCount validation errors"
        ) {
            val constraintViolations = validator.validate(dto)
            assertEquals(assertViolationCount, constraintViolations.size)
        }
    }

    internal class HexValidatorTestDto(
        @field:HexColor
        val hexValue: String
    )
}
