package nl.juraji.albums.util

import org.neo4j.driver.Record
import org.neo4j.driver.Value
import org.neo4j.driver.types.Node
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Utility to use data classes with Neo4j.
 *
 * @throws MissingPrimaryConstructorException When [T] does not have a primary constructor.
 */
class Neo4jDtoMapper<T : Any>(dtoClass: KClass<T>) {
    private val memberProperties = dtoClass.memberProperties
    private val primaryConstructor = dtoClass.primaryConstructor
        ?: throw MissingPrimaryConstructorException(dtoClass)

    /**
     * Map a [Record] to [T].
     *
     * @param record The [Record] to read from.
     * @param nodeLabel Optional node label, used in the Cypher query, to identify this node. Defaults to "n".
     * @throws RequiredPropertyNotFoundException When a property, which is required in [T], can not be found in the given [Record].
     * @throws CanNotMapValueException When a value can not be mapped, due to an unsupported type in [T].
     */
    fun recordToDto(record: Record, nodeLabel: String = "n"): T {
        val node = record.get(nodeLabel).asNode()

        val parameterValues = primaryConstructor.parameters
            .map { findNodeValueForParameter(node, it) }
            .toMap()

        return primaryConstructor.callBy(parameterValues)
    }

    /**
     * Create a [Map] of the given instance of [T].
     * Generally used as properties map in CREATE or MERGE Cypher queries.
     *
     * NULL values are omitted from the output map.
     *
     * @param dto An instance of [T].
     */
    fun dtoToPropertiesMap(dto: T): Map<String, Any> = memberProperties
        .mapNotNull { prop ->
            val value = prop.get(dto)
            if (value != null) prop.name to value
            else null
        }
        .toMap()

    private fun findNodeValueForParameter(node: Node, parameter: KParameter): Pair<KParameter, *> = when {
        node.containsKey(parameter.name) -> parameter to getNodeValue(node.get(parameter.name), parameter.type)
        parameter.type.isMarkedNullable -> parameter to null
        else -> throw RequiredPropertyNotFoundException(parameter, node)
    }

    private fun getNodeValue(value: Value, type: KType): Any = when (type.classifier) {
        Boolean::class -> value.asBoolean()
        ByteArray::class -> value.asByteArray()
        Double::class -> value.asDouble()
        Float::class -> value.asFloat()
        Int::class -> value.asInt()
        LocalDate::class -> value.asLocalDate()
        LocalDateTime::class -> value.asLocalDateTime()
        LocalTime::class -> value.asLocalTime()
        Long::class -> value.asLong()
        Number::class -> value.asNumber()
        Object::class -> value.asObject()
        OffsetDateTime::class -> value.asOffsetDateTime()
        OffsetTime::class -> value.asOffsetTime()
        String::class -> value.asString()
        ZonedDateTime::class -> value.asZonedDateTime()
        else -> throw CanNotMapValueException(value, type)
    }

    abstract class DtoMappingException(message: String) : Exception(message)

    class MissingPrimaryConstructorException(dtoClass: KClass<*>) :
        DtoMappingException("No primary constructor for ${dtoClass.qualifiedName}!")

    class RequiredPropertyNotFoundException(parameter: KParameter, node: Node) :
        DtoMappingException("Parameter ${parameter.name} is required, but not found in Node $node")

    class CanNotMapValueException(value: Value, type: KType) :
        DtoMappingException("Can not map value $value to ${type.classifier}")
}
