package nl.juraji.albums.domain.events

import nl.juraji.albums.util.LoggerCompanion
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

abstract class ReactiveEventListener {

    fun consumePublisher(operation: () -> CorePublisher<out Any>) {
        when (val publisher = operation.invoke()) {
            is Mono -> publisher.runCatching { block(HANDLE_TIMEOUT) }
            is Flux -> publisher.runCatching { blockLast(HANDLE_TIMEOUT) }
            else -> throw IllegalArgumentException("Can not consume core publisher of $publisher")
        }.onFailure { e -> logger.error("Error consuming Mono", e) }
    }

    companion object : LoggerCompanion(ReactiveEventListener::class) {
        val HANDLE_TIMEOUT: Duration = Duration.ofMinutes(5)
    }
}
