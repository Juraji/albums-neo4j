package nl.juraji.albums.api

import nl.juraji.albums.domain.TagsService
import nl.juraji.albums.domain.tags.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/tags")
class TagsController(
    private val tagsService: TagsService
) {

    @GetMapping
    fun getAllTags(): Flux<Tag> = tagsService.getAllTags()

    @PostMapping
    fun createTag(
        @Valid @RequestBody tag: Tag
    ): Mono<Tag> = tagsService.createTag(tag)

    @PutMapping("/{tagId}")
    fun updateTag(
        @PathVariable("tagId") tagId: String,
        @Valid @RequestBody tag: Tag
    ): Mono<Tag> = tagsService.updateTag(tagId, tag)

    @DeleteMapping("/{tagId}")
    fun deleteTag(
        @PathVariable("tagId") tagId: String
    ): Mono<Void> = tagsService.deleteTag(tagId)
}
