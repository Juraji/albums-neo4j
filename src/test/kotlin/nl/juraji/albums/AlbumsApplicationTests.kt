package nl.juraji.albums

import nl.juraji.albums.configurations.TestApiConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestApiConfiguration::class)
class AlbumsApplicationTests {

    @Test
    fun `context loads`() {
    }

}
