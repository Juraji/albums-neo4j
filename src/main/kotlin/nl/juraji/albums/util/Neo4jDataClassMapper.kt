package nl.juraji.albums.util

import org.neo4j.driver.types.MapAccessor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor

class Neo4jDataClassMapper<T : Any>(
    private val baseClass: KClass<out T>
) {
    private val nonNativeTypes: Set<KClass<*>> = buildNonNativeTypesList(baseClass)

    fun mapFrom(fetchResult: Map<String, Any>): T =
        mapFromInternal(fetchResult, baseClass)

    @Suppress("UNCHECKED_CAST")
    private fun <R : Any> mapFromInternal(mapProjection: Map<String, Any>, resultType: KClass<out R>): R {
        val primaryConstructor: KFunction<R> = resultType.primaryConstructor!!

        val parameters = primaryConstructor.parameters
            .map { kParameter ->
                val paramCls = kParameter.type.classifier as KClass<out Any>
                val value = mapProjection[kParameter.name]

                when {
                    value == null ->
                        if (kParameter.isOptional) kParameter to null
                        else throw CanNotMapNullException(paramCls)
                    paramCls.isInstance(value) -> kParameter to value
                    isEnum(paramCls) && value is String -> kParameter to getEnumValue(paramCls, value)
                    Number::class.isSuperclassOf(paramCls) && value is Number -> kParameter to value.toInt()
                    nonNativeTypes.contains(paramCls) -> {
                        val subMap = when (value) {
                            is MapAccessor -> value.asMap()
                            is Map<*, *> -> value as Map<String, Any>
                            else -> throw CanNotMapValueException(value, paramCls)
                        }
                        kParameter to mapFromInternal(subMap, paramCls)
                    }
                    else -> throw CanNotMapValueException(value, paramCls)
                }
            }
            .toMap()

        return primaryConstructor.callBy(parameters)
    }

    private fun isEnum(cls: KClass<*>): Boolean = cls.isSubclassOf(Enum::class)

    private fun getEnumValue(cls: KClass<*>, enumValue: String): Any =
        cls.java.enumConstants.filterIsInstance(Enum::class.java).first { it.name == enumValue }

    private fun buildNonNativeTypesList(cls: KClass<out Any>): Set<KClass<*>> {
        assert(cls.isData)
        val selfConstructor = cls.primaryConstructor!!
        val childAllowedTypes = selfConstructor.parameters
            .map { it.type.classifier as KClass<out Any> }
            .filter { it.isData }
            .flatMap { buildNonNativeTypesList(it) }
            .toSet()

        return childAllowedTypes + cls
    }

    class CanNotMapValueException(value: Any, targetType: KClass<out Any>) :
        Exception("Can not map value [$value] to parameter of type ${targetType.qualifiedName}")

    class CanNotMapNullException(targetType: KClass<out Any>) :
        Exception("Value for parameter ${targetType.qualifiedName} is null, but the parameter is not nullable")
}
