package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.pictures.DuplicatesView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class DuplicatesController(
    private val duplicatesService: DuplicatesService
) {

    @GetMapping("/duplicates")
    fun getAllDuplicates(): Flux<DuplicatesView> = duplicatesService.getAll()

}
