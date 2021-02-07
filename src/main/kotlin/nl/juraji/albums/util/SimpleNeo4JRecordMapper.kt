package nl.juraji.albums.util

import org.neo4j.driver.Record
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

open class SimpleNeo4JRecordMapper {

    inline fun <reified T : Any> mapToDataClass(record: Record, label: String = "root"): T =
        SimpleNeo4JRecordMapper().mapToDataClass(T::class, record, label)

    /**
     * Map a [Record] to [T], using [root] as entity.
     * Any sub-entities can be mapped by naming them in the query output
     */
    fun <T : Any> mapToDataClass(type: KClass<T>, record: Record, label: String = "root"): T {
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
            mapper == null -> parameter to mapToDataClass(
                parameter.type.classifier as KClass<Any>,
                record,
                parameter.name!!
            )
            values.containsKey(parameter.name) -> parameter to mapper.invoke(values[parameter.name]!!)
            parameter.type.isMarkedNullable -> parameter to null
            else -> throw RequiredPropertyNotFoundException(parameter, values)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findParameterMapper(type: KType): ((Any) -> Any)? = when (type.classifier) {
        Boolean::class -> ({ it as Boolean })
        ByteArray::class -> ({ it as ByteArray })
        Double::class -> ({ it as Double })
        Float::class -> ({ it as Float })
        Int::class -> ({ it as Int })
        Long::class -> ({ it as Long })
        String::class -> ({ it as String })
        LocalDateTime::class -> ({ it as LocalDateTime })
        LocalDate::class -> ({ it as LocalDate })
        LocalTime::class -> ({ it as LocalTime })
        else -> when {
            // Special case for JVM Enums
            (type.classifier as KClass<*>).isSubclassOf(Enum::class) -> ({
                val enumClass = (type.classifier as KClass<*>).java as Class<Enum<*>>
                java.lang.Enum.valueOf(enumClass, it as String)
            })
            else -> null
        }
    }

    abstract class MappingException(message: String) : Exception(message)

    class MissingPrimaryConstructorException(dtoClass: KClass<*>) :
        MappingException("No primary constructor for ${dtoClass.qualifiedName}!")

    class RequiredPropertyNotFoundException(parameter: KParameter, source: Any) :
        MappingException("Parameter ${parameter.name} is required, but not found in value source $source")
}
