package nl.juraji.albums.configurations

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.ServerCodecConfigurer

@TestConfiguration
class TestApiConfiguration {

    @Bean
    fun errorAttributes(): ErrorAttributes = DefaultErrorAttributes()

    @Bean
    fun resources(): WebProperties.Resources = WebProperties.Resources()

    @Bean
    fun serverCodecConfigurer(): ServerCodecConfigurer = ServerCodecConfigurer.create()
}
