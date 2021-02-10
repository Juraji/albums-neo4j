package nl.juraji.albums.util

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

fun Mono<out Any>.mapToUnit(): Mono<Unit> = this.flatMap { Mono.empty() }

fun <T> deferTo(scheduler: Scheduler, supplier: () -> T?): Mono<T> =
    Mono.defer { Mono.justOrEmpty(supplier()) }.subscribeOn(scheduler)

fun <T> deferOptionalTo(scheduler: Scheduler, supplier: () -> Optional<T>): Mono<T> =
    Mono.defer { Mono.justOrEmpty(supplier()) }.subscribeOn(scheduler)

fun <T> deferIterableTo(scheduler: Scheduler, supplier: () -> Iterable<T>): Flux<T> =
    Flux.defer { Flux.fromIterable(supplier()) }.subscribeOn(scheduler)
