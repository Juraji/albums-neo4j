package nl.juraji.albums.api.dto

abstract class ErrorDto(
    val type: ErrorType,
    open val message: String
)

data class GenericErrorDto(
    override val message: String
) : ErrorDto(ErrorType.GENERIC, message)

data class ValidationErrorDto(
    override val message: String
) : ErrorDto(ErrorType.VALIDATION, message)

data class FieldValidationErrorDto(
    override val message: String,
    val fieldErrors: Map<String, String>
) : ErrorDto(ErrorType.FIELD_VALIDATION, message)

enum class ErrorType {
    GENERIC, VALIDATION, FIELD_VALIDATION
}
