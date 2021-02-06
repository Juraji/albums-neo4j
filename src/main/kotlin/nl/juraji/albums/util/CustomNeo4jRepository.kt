package nl.juraji.albums.util

import org.neo4j.driver.Record
import java.lang.Enum.valueOf
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

abstract class CustomNeo4jRepository : Neo4jMappingSupport {

    inline fun <reified T : Any> autoMapDataClass(record: Record, label: String = "root"): T =
        autoMapDataClass(T::class, record, label)

    /**
     * Map a [Record] to [T], using [root] as entity.
     * Any sub-entities can be mapped by naming them in the query output
     */
    fun <T : Any> autoMapDataClass(type: KClass<T>, record: Record, label: String = "root"): T {
        val primaryConstructor = type.primaryConstructor
            ?: throw MissingPrimaryConstructorException(type)
        val values = record[label].asMap()

        val parameterValues = primaryConstructor.parameters
            .map { findValueForParameter(values, it, record) }
            .toMap()

        return primaryConstructor.callBy(parameterValues)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findValueForParameter(
        values: Map<String, Any>,
        parameter: KParameter,
        record: Record
    ): Pair<KParameter, *> {
        val mapper = findParameterMapper(parameter.type)

        return when {
            mapper == null -> parameter to autoMapDataClass(
                parameter.type.classifier as KClass<Any>,
                record,
                parameter.name!!
            )
            values.containsKey(parameter.name) -> parameter to mapper.invoke(values[parameter.name]!!)
            parameter.type.isMarkedNullable -> parameter to null
            else -> throw RequiredPropertyNotFoundException(parameter, values)
        }
    }

    private fun findParameterMapper(type: KType): ((Any) -> Any)? = when (type.classifier) {
        Boolean::class -> this::asBoolean
        ByteArray::class -> this::asByteArray
        Double::class -> this::asDouble
        Float::class -> this::asFloat
        Int::class -> this::asInt
        Long::class -> this::asLong
        String::class -> this::asString
        LocalDate::class -> this::asLocalDate
        LocalDateTime::class -> this::asLocalDateTime
        LocalTime::class -> this::asLocalTime
        OffsetDateTime::class -> this::asOffsetDateTime
        OffsetTime::class -> this::asOffsetTime
        ZonedDateTime::class -> this::asZonedDateTime
        else -> when {
            isEnumType(type) -> ({ asEnumFor(type, it as String) })
            else -> null
        }
    }

    abstract class MappingException(message: String) : Exception(message)

    class MissingPrimaryConstructorException(dtoClass: KClass<*>) :
        MappingException("No primary constructor for ${dtoClass.qualifiedName}!")

    class RequiredPropertyNotFoundException(parameter: KParameter, source: Any) :
        MappingException("Parameter ${parameter.name} is required, but not found in value source $source")
}

private interface Neo4jMappingSupport {
    // Primitives
    fun asBoolean(value: Any) = value as Boolean
    fun asByteArray(value: Any) = value as ByteArray
    fun asDouble(value: Any) = value as Double
    fun asFloat(value: Any) = value as Float
    fun asInt(value: Any) = value as Int
    fun asLong(value: Any) = value as Long
    fun asString(value: Any) = value as String

    // Java Time
    fun asLocalDateTime(value: Any): LocalDateTime = LocalDateTime.parse(value as String)
    fun asLocalDate(value: Any): LocalDate = LocalDate.parse(value as String)
    fun asLocalTime(value: Any): LocalTime = LocalTime.parse(value as String)
    fun asOffsetDateTime(value: Any): OffsetDateTime = OffsetDateTime.parse(value as String)
    fun asOffsetTime(value: Any): OffsetTime = OffsetTime.parse(value as String)
    fun asZonedDateTime(value: Any): ZonedDateTime = ZonedDateTime.parse(value as String)

    // JVM Enum
    fun isEnumType(type: KType): Boolean = (type.classifier as KClass<*>).isSubclassOf(Enum::class)
    fun asEnumFor(type: KType, value: Any): Enum<*> {
        @Suppress("UNCHECKED_CAST") val enumClass = (type.classifier as KClass<*>).java as Class<Enum<*>>
        return valueOf(enumClass, value as String)
    }
}
