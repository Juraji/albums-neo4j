package nl.juraji.albums.configuration.converters

import org.neo4j.driver.Value
import org.neo4j.driver.internal.value.BytesValue
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import java.util.*

class Neo4jBitSetValueConverter : GenericConverter {
    override fun getConvertibleTypes(): Set<GenericConverter.ConvertiblePair> = setOf(
        GenericConverter.ConvertiblePair(BitSet::class.java, Value::class.java),
        GenericConverter.ConvertiblePair(Value::class.java, BitSet::class.java)
    )

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? = when (source) {
        is BitSet -> BytesValue(source.toByteArray())
        is Value -> BitSet.valueOf(source.asByteArray())
        else -> null
    }
}
