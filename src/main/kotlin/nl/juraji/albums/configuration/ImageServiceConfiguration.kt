package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("image-service")
data class ImageServiceConfiguration(
    val hashSampleSize: Int = 100,
    val hashSize: Int = hashSampleSize * hashSampleSize,
    val similarityThreshold: Double = 0.82,
    val thumbnailSize: Int = 250,
    val thumbnailsDirectory: String = "./",
    val picturesDirectory: String = "./",
)
