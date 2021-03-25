package nl.juraji.albums.util.validators

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Constraint(validatedBy = [HexColorValidator::class])
annotation class HexColor(
    val message: String = "Invalid hexadecimal color notation",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class HexColorValidator : ConstraintValidator<HexColor, String> {

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean =
        value.matches(HEX_COLOR_PATTERN)

    companion object {
        val HEX_COLOR_PATTERN = "^#[0-9a-f]{6}$".toRegex()
    }

}
