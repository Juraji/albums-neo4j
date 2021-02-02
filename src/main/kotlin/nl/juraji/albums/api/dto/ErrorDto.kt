package nl.juraji.albums.api.dto

interface ErrorDto {
    val type: ErrorType
    val requestId: String
    val status: Int
    val message: String
}

data class GenericErrorDto(
    override val type: ErrorType = ErrorType.GENERIC,
    override val message: String,
    override val requestId: String,
    override val status: Int
) : ErrorDto

data class ValidationErrorDto(
    override val type: ErrorType = ErrorType.VALIDATION,
    override val message: String,
    override val requestId: String,
    override val status: Int
) : ErrorDto

data class FieldValidationErrorDto(
    override val type: ErrorType = ErrorType.FIELD_VALIDATION,
    override val message: String,
    override val requestId: String,
    override val status: Int,
    val fieldErrors: Map<String, String>
) : ErrorDto

enum class ErrorType {
    GENERIC, VALIDATION, FIELD_VALIDATION
}
