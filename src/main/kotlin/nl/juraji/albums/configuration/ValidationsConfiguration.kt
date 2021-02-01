package nl.juraji.albums.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean


@Configuration
class ValidationsConfiguration {

    @Bean
    fun localValidatorFactoryBean(messageSource: MessageSource): LocalValidatorFactoryBean {
        val bean = LocalValidatorFactoryBean()
        bean.setValidationMessageSource(messageSource)
        return bean
    }
}
