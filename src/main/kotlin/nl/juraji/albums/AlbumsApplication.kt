package nl.juraji.albums

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("nl.juraji.albums.configuration")
class AlbumsApplication

fun main(args: Array<String>) {
    runApplication<AlbumsApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
