package nl.juraji.albums.configuration

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("rate-limits.image-upload")
data class ImageUploadRateLimitsConfiguration(
    val limit: Long,
    val refillTimeMs: Long
) {

    @Bean
    @Qualifier("imageUploadRateLimiter")
    fun imageUploadRateLimiter(): Bucket = Bucket4j.builder()
        .addLimit(Bandwidth.simple(limit, Duration.ofMillis(refillTimeMs)))
        .build()
}