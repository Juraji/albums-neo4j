package nl.juraji.albums.api.exceptions

import nl.juraji.albums.api.dto.ErrorDto
import nl.juraji.albums.api.dto.FieldValidationErrorDto
import nl.juraji.albums.api.dto.GenericErrorDto
import nl.juraji.albums.api.dto.ValidationErrorDto
import nl.juraji.reactor.validations.ValidationException
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono


@Configuration
@Order(-2)
@Suppress("LeakingThis")
class GlobalErrorWebExceptionHandler(
    errorAttributes: ErrorAttributes,
    resources: WebProperties.Resources,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, resources, applicationContext) {

    init {
        setMessageWriters(serverCodecConfigurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse)
    }

    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val requestId = request.exchange().request.id

        val dtoBody: ErrorDto = when (val error = getError(request)) {
            is WebExchangeBindException -> handleWebExchangeBindException(error, requestId)
            is ValidationException -> handleValidationException(error, requestId)
            is ResponseStatusException -> handleResponseStatusException(error, requestId)
            is RateLimitExceededException -> handleRateLimitExceededException(error, requestId)
            else -> handleOther(requestId)
        }

        return ServerResponse.status(dtoBody.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromProducer(Mono.just(dtoBody), ErrorDto::class.java))
    }

    private fun handleWebExchangeBindException(error: WebExchangeBindException, requestId: String): ErrorDto {
        val errors = error.bindingResult.allErrors
            .associate { (it as FieldError).field to it.defaultMessage!! }

        return FieldValidationErrorDto(
            message = "Validatiefout",
            status = HttpStatus.BAD_REQUEST,
            requestId = requestId,
            fieldErrors = errors
        )
    }

    private fun handleValidationException(error: ValidationException, requestId: String): ErrorDto {
        return ValidationErrorDto(
            message = error.localizedMessage,
            status = HttpStatus.BAD_REQUEST,
            requestId = requestId,
        )
    }

    private fun handleRateLimitExceededException(error: RateLimitExceededException, requestId: String): ErrorDto {
        return GenericErrorDto(
            message = error.localizedMessage,
            status = HttpStatus.TOO_MANY_REQUESTS,
            requestId = requestId
        )
    }

    private fun handleResponseStatusException(error: ResponseStatusException, requestId: String): ErrorDto {
        return GenericErrorDto(
            message = error.mostSpecificCause.localizedMessage,
            status = error.status,
            requestId = requestId
        )
    }

    private fun handleOther(requestId: String): ErrorDto {
        return GenericErrorDto(
            message = "Er is een onbekende fout opgetreden",
            requestId = requestId,
            status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
