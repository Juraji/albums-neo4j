package nl.juraji.albums.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.format.DateTimeFormatter

@Configuration
class JacksonObjectMapperConfiguration {

    @Bean
    @Primary
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
            .modules(
                KotlinModule()
            )
            .propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializers(
                LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME),
            )
            .deserializers(
                LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME)
            )
            .featuresToEnable(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
                SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
            )
    }

    @Bean
    @Primary
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper =
        builder.build()
}
