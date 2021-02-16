package nl.juraji.albums

import nl.juraji.albums.configurations.TestApiConfiguration
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Disabled
@SpringBootTest
@Import(TestApiConfiguration::class)
class AlbumsApplicationTests {

    @Test
    fun `context loads`() {
    }

}
