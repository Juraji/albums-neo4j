package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("image-service")
data class ImageServiceConfiguration(
    val hashSampleSize: Int,
    val hashSize: Int = hashSampleSize * hashSampleSize,
    val similarityThreshold: Double,
    val thumbnailSize: Int,
    val thumbnailsDirectory: String,
    val picturesDirectory: String,
)
