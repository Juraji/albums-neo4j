package nl.juraji.albums.util.kotlin

import org.springframework.context.ApplicationEventPublisher

fun ApplicationEventPublisher.publishEventAndForget(event: Any) {
    this.runCatching { publishEvent(event) }
}
