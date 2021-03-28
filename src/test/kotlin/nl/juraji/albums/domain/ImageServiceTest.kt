package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.painters.RadialGradient
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import nl.juraji.albums.configuration.ImageServiceConfiguration
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.Part
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.awt.Color
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ImageServiceTest {

    @SpyK
    private var configuration = ImageServiceConfiguration()

    @InjectMockKs
    private lateinit var imageService: ImageService

    @Test
    fun `should load file part as image`() {
        val resource = ClassPathResource("/test-image.jpg")
        val part = object : Part {
            override fun name(): String = "files[]"
            override fun headers(): HttpHeaders = HttpHeaders()
            override fun content(): Flux<DataBuffer> = Flux.just(resource)
                .map { it.inputStream.readAllBytes() }
                .map { DefaultDataBufferFactory.sharedInstance.wrap(it) }
        }

        val result = imageService.loadPartAsImage(part)

        StepVerifier.create(result)
            .expectNextMatches { it.width == 23 && it.height == 21 && it.type == 5 }
            .verifyComplete()
    }

    @Test
    fun `should load resource as image`() {
        val resource = ClassPathResource("/test-image.jpg")

        val result = imageService.loadResourceAsImage(resource)

        StepVerifier.create(result)
            .expectNextMatches { it.width == 23 && it.height == 21 && it.type == 5 }
            .verifyComplete()
    }

    @Test
    @Disabled
    fun `should save thumbnail`() {
        TODO("Figure out how to test")
    }

    @Test
    @Disabled
    fun `should save picture`() {
        TODO("Figure out how to test")
    }

    @Test
    fun `should generate hash`() {
        val image = ImmutableImage.create(100, 100)
            .fill(
                RadialGradient(
                    50f,
                    50f,
                    100f,
                    floatArrayOf(0f, 0.25f, 0.50f, 0.75f, 1f),
                    arrayOf(Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK)
                )
            )
        val expected = with("APgPgP/Aww8/8PgDzj/A/APox4E+PPDDBx988MEHH3zwwQc/fODHg/4MyD+A+AMMPyA=") {
            BitSet.valueOf(Base64.getDecoder().decode(this))
        }

        every { configuration.hashSampleSize } returns 20

        val result = imageService.generateHash(image)

        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()

    }
}
