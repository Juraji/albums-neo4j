package nl.juraji.albums.util

import reactor.core.publisher.Mono

fun Mono<Void>.toUnit(): Mono<Unit> = this.map {}
