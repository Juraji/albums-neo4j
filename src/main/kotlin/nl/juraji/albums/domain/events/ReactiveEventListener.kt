package nl.juraji.albums.domain.events

import nl.juraji.albums.util.kotlin.LoggerCompanion
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class ReactiveEventListener {

    fun consumePublisher(operation: () -> CorePublisher<out Any>) {
        when (val publisher = operation.invoke()) {
            is Mono -> publisher.onErrorContinue { e, _ -> logger.error("Error consuming Mono", e) }.subscribe()
            is Flux -> publisher.onErrorContinue { e, _ -> logger.error("Error consuming Flux", e) }.subscribe()
            else -> throw IllegalArgumentException("Can not consume core publisher of $publisher")
        }
    }

    companion object : LoggerCompanion(ReactiveEventListener::class)
}
