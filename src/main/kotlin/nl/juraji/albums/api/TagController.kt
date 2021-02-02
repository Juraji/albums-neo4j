package nl.juraji.albums.api

import nl.juraji.albums.model.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

    @GetMapping
    fun getAllTags(): Flux<Tag> = tagService.getAllTags()

    @PostMapping
    fun createTag(
        @Valid @RequestBody tag: Tag
    ): Mono<Tag> = tagService.createTag(tag)

    @DeleteMapping("/{tagId}")
    fun deleteTag(
        @PathVariable("tagId") tagId: String
    ): Mono<Unit> = tagService.deleteTag(tagId)
}
