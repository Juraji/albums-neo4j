package nl.juraji.albums.util.validators

import java.nio.file.Paths
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Constraint(validatedBy = [ValidFileSystemPathValidator::class])
annotation class ValidFileSystemPath(
    val message: String = "Invalid path",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class ValidFileSystemPathValidator : ConstraintValidator<ValidFileSystemPath, String> {

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean =
        runCatching { Paths.get(value) }.isSuccess

}
