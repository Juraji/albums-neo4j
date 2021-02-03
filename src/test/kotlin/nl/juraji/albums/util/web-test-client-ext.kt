package nl.juraji.albums.util

import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriBuilder

fun <S : WebTestClient.RequestHeadersSpec<*>> WebTestClient.UriSpec<S>.uriBuilder(block: UriBuilder.() -> Unit): S =
    this.uri { it.apply(block).build() }
