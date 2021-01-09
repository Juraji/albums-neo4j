package nl.juraji.albums.query.repositories

import org.neo4j.driver.Driver
import org.neo4j.driver.Record
import org.neo4j.driver.summary.ResultSummary
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

abstract class Repository(
    private val driver: Driver
) {

    private fun read(
        query: String,
        parameters: Map<String, Any>
    ): Publisher<Record> = driver.rxSession().readTransaction { tx -> tx.run(query, parameters).records() }

    /**
     * Query and read a single record.
     *
     * @param query A Cypher query string.
     * @param parameters a [Map] of parameter values used in above string.
     */
    protected fun readSingle(
        query: String,
        parameters: Map<String, Any> = emptyMap()
    ): Mono<Record> = read(query, parameters).toMono()

    /**
     * Query and read multiple records.
     *
     * @param query A Cypher query string.
     * @param parameters a [Map] of parameter values used in above string.
     */
    protected fun readMultiple(
        query: String,
        parameters: Map<String, Any> = emptyMap()
    ): Flux<Record> = read(query, parameters).toFlux()

    /**
     * Execute a writing query and return the query summary.
     *
     * @param query A Cypher query string.
     * @param parameters a [Map] of parameter values used in above string.
     */
    protected fun write(
        query: String,
        parameters: Map<String, Any>
    ): Mono<ResultSummary> = driver.rxSession().writeTransaction { tx -> tx.run(query, parameters).consume() }.toMono()

    /**
     * Execute a writing query and return a single record.
     *
     * @param query A Cypher query string.
     * @param parameters a [Map] of parameter values used in above string.
     */
    protected fun writeAndReturn(
        query: String,
        parameters: Map<String, Any>
    ): Mono<Record> = driver.rxSession().writeTransaction { tx -> tx.run(query, parameters).records() }.toMono()
}
