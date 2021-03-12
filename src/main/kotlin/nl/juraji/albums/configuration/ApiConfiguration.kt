package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.time.Duration


@Configuration
class ApiConfiguration {

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
        configuration.allowedMethods = listOf("GET", "POST", "DELETE")

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
