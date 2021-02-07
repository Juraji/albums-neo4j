package nl.juraji.albums.domain

import nl.juraji.albums.util.LoggerCompanion
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

abstract class EventListenerService {

    fun handleAsMono(operation: () -> Mono<out Any>) {
        operation.invoke().runCatching { block(HANDLE_TIMEOUT) }
            .onFailure { e -> logger.error("Error during handling of Mono EventListener", e) }

    }

    fun handleAsFlux(operation: () -> Flux<out Any>) {
        operation.invoke().runCatching { blockLast(HANDLE_TIMEOUT) }
            .onFailure { e -> logger.error("Error during handling of Flux EventListener", e) }
    }

    companion object : LoggerCompanion(EventListenerService::class) {
        val HANDLE_TIMEOUT: Duration = Duration.ofMinutes(5)
    }
}
