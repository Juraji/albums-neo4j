package nl.juraji.albums.api

import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.pictures.DuplicatesView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/duplicates")
class DuplicatesController(
    private val duplicatesService: DuplicatesService
) {

    @GetMapping
    fun getAllDuplicates(): Flux<DuplicatesView> = duplicatesService.getAll()

    @GetMapping("/unlink-history")
    fun getUnlinkHistory(): Flux<DuplicatesView> = duplicatesService.getUnlinkHistory()

    @PostMapping("/run-scan")
    fun runDuplicateScan(): Flux<DuplicatesView> = duplicatesService.scanDuplicates()
}
