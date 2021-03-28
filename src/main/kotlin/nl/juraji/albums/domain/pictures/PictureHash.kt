package nl.juraji.albums.domain.pictures

import org.neo4j.driver.Value
import org.neo4j.driver.internal.value.BytesValue
import org.springframework.data.neo4j.core.convert.ConvertWith
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.util.*

data class PictureHash(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    val id: String? = null,
    @ConvertWith(converter = BitSetConverter::class)
    val data: BitSet
)

class BitSetConverter : Neo4jPersistentPropertyConverter<BitSet> {
    override fun write(source: BitSet): Value = BytesValue(source.toByteArray())
    override fun read(source: Value): BitSet = BitSet.valueOf(source.asByteArray())
}
