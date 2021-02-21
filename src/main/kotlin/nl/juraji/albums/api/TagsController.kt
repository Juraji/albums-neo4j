package nl.juraji.albums.api

import nl.juraji.albums.api.dto.NewTagDto
import nl.juraji.albums.domain.TagService
import nl.juraji.albums.domain.tags.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/tags")
class TagsController(
    private val tagService: TagService
) {

    @GetMapping
    fun getAllTags(): Flux<Tag> = tagService.getAllTags()

    @PostMapping
    fun createTag(
        @Valid @RequestBody tag: NewTagDto
    ): Mono<Tag> = tagService.createTag(tag.label, tag.color, tag.textColor)

    @DeleteMapping("/{tagId}")
    fun deleteTag(
        @PathVariable("tagId") tagId: String
    ): Mono<Unit> = tagService.deleteTag(tagId)
}
