package nl.juraji.albums.domain.events

import nl.juraji.albums.util.LoggerCompanion
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class ReactiveEventListener {

    fun consumePublisher(operation: () -> CorePublisher<out Any>) {
        when (val publisher = operation.invoke()) {
            is Mono -> publisher.subscribe(null, { e -> logger.error("Error consuming Mono", e) })
            is Flux -> publisher.subscribe(null, { e -> logger.error("Error consuming Flux", e) })
            else -> throw IllegalArgumentException("Can not consume core publisher of $publisher")
        }
    }

    companion object : LoggerCompanion(ReactiveEventListener::class)
}
