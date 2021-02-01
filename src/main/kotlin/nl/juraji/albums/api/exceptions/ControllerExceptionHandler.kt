package nl.juraji.albums.api.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import nl.juraji.albums.api.dto.ErrorDto
import nl.juraji.albums.api.dto.FieldValidationErrorDto
import nl.juraji.albums.api.dto.GenericErrorDto
import nl.juraji.albums.api.dto.ValidationErrorDto
import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.reactor.validations.ValidationException
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Order(-2)
@Configuration
class ControllerExceptionHandler(
    private val objectMapper: ObjectMapper,
) : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, e: Throwable): Mono<Void> {
        val (status, dtoBody) = when (e) {
            is MethodArgumentNotValidException -> handleMethodArgumentNotValidException(e)
            is ValidationException -> handleValidationException(e)
            else -> handleOther(e)
        }

        exchange.response.statusCode = status

        return if (exchange.request.headers.accept.contains(MediaType.APPLICATION_JSON)) {
            val body: Mono<DataBuffer> = Mono
                .just(dtoBody)
                .map { objectMapper.writeValueAsBytes(it) }
                .map { exchange.response.bufferFactory().wrap(it) }

            exchange.response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            exchange.response.writeWith(body)
        } else {
            exchange.response.setComplete()
        }
    }

    private fun handleValidationException(e: ValidationException): Pair<HttpStatus, ErrorDto> {
        return HttpStatus.BAD_REQUEST to ValidationErrorDto(e.localizedMessage)
    }

    private fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): Pair<HttpStatus, ErrorDto> {
        val errors = e.bindingResult.allErrors
            .map { (it as FieldError).field to it.defaultMessage!! }
            .toMap()

        val errorDto = FieldValidationErrorDto(
            message = "Validatiefout",
            fieldErrors = errors
        )

        return HttpStatus.BAD_REQUEST to errorDto
    }

    private fun handleOther(e: Throwable): Pair<HttpStatus, ErrorDto> {
        logger.error("An error occurred during handling of a request", e)

        return HttpStatus.INTERNAL_SERVER_ERROR to GenericErrorDto("Er is een onbekende fout opgetreden")
    }

    companion object : LoggerCompanion(ControllerExceptionHandler::class)
}
