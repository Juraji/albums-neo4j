package nl.juraji.albums.util

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKMatcherScope
import io.mockk.MockKStubScope
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

fun <T> MockKMatcherScope.anyFunction() = any<() -> T>()

fun <T, B> MockKStubScope<T, B>.runsCallback(): MockKAdditionalAnswerScope<T, B> =
    this answers { lastArg<() -> T>().invoke() }

@Suppress("UNCHECKED_CAST")
fun <T: Any, B> MockKStubScope<Mono<T>, B>.returnsArgumentAsMono(n: Int = 0): MockKAdditionalAnswerScope<Mono<T>, B> =
    this answers { Mono.just(invocation.args[n] as T) }

fun <T, B> MockKStubScope<Optional<T>, B>.returnsEmptyOptional(): MockKAdditionalAnswerScope<Optional<T>, B> =
    this.returns(Optional.empty())

fun <T, B> MockKStubScope<Mono<T>, B>.returnsEmptyMono(): MockKAdditionalAnswerScope<Mono<T>, B> =
    this.returns(Mono.empty())

fun <B> MockKStubScope<Mono<Void>, B>.returnsVoidMono(): MockKAdditionalAnswerScope<Mono<Void>, B> =
    this.returns(Mono.create { it.success(null) })

infix fun <T, B> MockKStubScope<Optional<T>, B>.returnsOptionalOf(value: T?): MockKAdditionalAnswerScope<Optional<T>, B> =
    this.returns(Optional.ofNullable(value))

infix fun <T : Any, B> MockKStubScope<Mono<T>, B>.returnsMonoOf(value: T): MockKAdditionalAnswerScope<Mono<T>, B> =
    this.returns(Mono.just(value))

infix fun <T, B> MockKStubScope<Mono<T>, B>.returnsManyMonoOf(iterable: Iterable<T?>): MockKAdditionalAnswerScope<Mono<T>, B> =
    this.returnsMany(iterable.map { Mono.justOrEmpty(it) })

infix fun <T, B> MockKStubScope<Mono<T>, B>.returnsErrorMonoOf(t: () -> Throwable): MockKAdditionalAnswerScope<Mono<T>, B> =
    this.returns(Mono.error(t))

infix fun <T : Any, B> MockKAdditionalAnswerScope<Mono<T>, B>.andThenMonoOf(value: T): MockKAdditionalAnswerScope<Mono<T>, B> =
    this.andThen(Mono.just(value))

fun <T, B> MockKStubScope<Flux<T>, B>.returnsEmptyFlux(): MockKAdditionalAnswerScope<Flux<T>, B> =
    this.returns(Flux.empty())

infix fun <T, B> MockKStubScope<Flux<T>, B>.returnsFluxOf(iterable: Iterable<T>): MockKAdditionalAnswerScope<Flux<T>, B> =
    this.returns(Flux.fromIterable(iterable))

infix fun <T : Any, B> MockKStubScope<Flux<T>, B>.returnsFluxOf(singleItem: T): MockKAdditionalAnswerScope<Flux<T>, B> =
    this.returns(Flux.just(singleItem))