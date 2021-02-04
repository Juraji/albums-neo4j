package nl.juraji.albums.util

import org.intellij.lang.annotations.Language
import org.springframework.core.annotation.AliasFor
import org.springframework.data.neo4j.repository.query.Query

/**
 * Alias annotation for [Query] with [Language] annotation on value to enable syntax highlighting
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Query
annotation class CypherQuery(
    @Language("CYPHER")
    @get:AliasFor(attribute = "value", annotation = Query::class)
    val value: String,
    @Language("CYPHER")
    @get:AliasFor(attribute = "countQuery", annotation = Query::class)
    val countQuery: String = "",
    @get:AliasFor(attribute = "count", annotation = Query::class)
    val count: Boolean = false,
    @get:AliasFor(attribute = "exists", annotation = Query::class)
    val exists: Boolean = false,
    @get:AliasFor(attribute = "delete", annotation = Query::class)
    val delete: Boolean = false
)
