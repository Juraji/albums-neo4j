package nl.juraji.albums.util

import org.springframework.test.util.AssertionErrors
import org.springframework.test.web.reactive.server.EntityExchangeResult

fun <T> EntityExchangeResult<T>.assertEqualsTo(expected: T): EntityExchangeResult<T> {
    this.assertWithDiagnostics { AssertionErrors.assertEquals("Response body", expected, this.responseBody) }
    return this
}
