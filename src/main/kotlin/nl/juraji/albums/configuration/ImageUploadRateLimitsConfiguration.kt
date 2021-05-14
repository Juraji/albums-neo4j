package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("rate-limits.image-upload")
data class ImageUploadRateLimitsConfiguration(
    val maxConcurrent: Int,
)