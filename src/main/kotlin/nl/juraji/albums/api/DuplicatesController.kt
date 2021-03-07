package nl.juraji.albums.api

import nl.juraji.albums.api.dto.DuplicateProps
import nl.juraji.albums.api.dto.toDuplicateProps
import nl.juraji.albums.domain.DuplicatesService
import nl.juraji.albums.domain.duplicates.Duplicate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/duplicates")
class DuplicatesController(
    private val duplicatesService: DuplicatesService
) {

    @GetMapping
    fun getAllDuplicates(): Flux<DuplicateProps> = duplicatesService
        .findAllDuplicates()
        .map(Duplicate::toDuplicateProps)

    @DeleteMapping("/{duplicateId}")
    fun deleteDuplicate(
        @PathVariable duplicateId: String
    ): Mono<Void> = duplicatesService.deleteById(duplicateId)
}
