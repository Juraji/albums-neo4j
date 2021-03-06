package nl.juraji.albums.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("duplicate-scanner")
data class DuplicateScannerConfiguration(
    val hashSampleSize: Int = 100,
    val hashSize: Int = hashSampleSize * hashSampleSize,
    val similarityThreshold: Double = 0.82
)
