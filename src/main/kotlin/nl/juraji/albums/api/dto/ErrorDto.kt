package nl.juraji.albums.api.dto

import org.springframework.http.HttpStatus

interface ErrorDto {
    val type: ErrorType
    val requestId: String
    val status: HttpStatus
    val message: String
}

data class GenericErrorDto(
    override val type: ErrorType = ErrorType.GENERIC,
    override val message: String,
    override val requestId: String,
    override val status: HttpStatus
) : ErrorDto

data class ValidationErrorDto(
    override val type: ErrorType = ErrorType.VALIDATION,
    override val message: String,
    override val requestId: String,
    override val status: HttpStatus
) : ErrorDto

data class FieldValidationErrorDto(
    override val type: ErrorType = ErrorType.FIELD_VALIDATION,
    override val message: String,
    override val requestId: String,
    override val status: HttpStatus,
    val fieldErrors: Map<String, String>
) : ErrorDto

enum class ErrorType {
    GENERIC, VALIDATION, FIELD_VALIDATION
}
