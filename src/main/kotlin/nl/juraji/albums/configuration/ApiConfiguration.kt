package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader
import org.springframework.http.codec.multipart.MultipartHttpMessageReader
import org.springframework.util.unit.DataSize
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.time.Duration


@Configuration
class ApiConfiguration : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        val partReader = DefaultPartHttpMessageReader()
        partReader.setMaxHeadersSize(DataSize.ofMegabytes(8).toBytes().toInt()) // 9 KiB, default is 8 KiB

        partReader.isEnableLoggingRequestDetails = true

        val multipartReader = MultipartHttpMessageReader(partReader)
        multipartReader.isEnableLoggingRequestDetails = true

        configurer.defaultCodecs().multipartReader(multipartReader)
    }

    @Bean
    @Primary
    fun localValidatorFactoryBean(messageSource: MessageSource): LocalValidatorFactoryBean {
        val bean = LocalValidatorFactoryBean()
        bean.setValidationMessageSource(messageSource)
        return bean
    }

    @Bean
    @Primary
    fun corsWebFilter(
        appConfiguration: ApplicationCorsConfiguration
    ): CorsWebFilter {
        val configuration = CorsConfiguration()
        configuration.maxAge = appConfiguration.maxAge
        configuration.allowedOrigins = appConfiguration.allowedOrigins
        configuration.allowedMethods = listOf(
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
        ).map(HttpMethod::name)
        configuration.allowedHeaders = listOf(
            HttpHeaders.CONTENT_TYPE
        )

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return CorsWebFilter(source)
    }
}

@ConstructorBinding
@ConfigurationProperties("spring.webflux.cors")
data class ApplicationCorsConfiguration(
    val maxAge: Long = Duration.ofHours(3).seconds,
    val allowedOrigins: List<String> = emptyList(),
)
