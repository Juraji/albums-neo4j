package nl.juraji.albums.api

import nl.juraji.albums.model.Tag
import nl.juraji.albums.repositories.TagRepository
import nl.juraji.albums.util.toUnit
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    fun getAllTags(): Flux<Tag> = tagRepository.findAll()

    fun createTag(tag: Tag): Mono<Tag> = Mono.just(tag)
        .validateAsync {
            synchronous {
                isTrue(it.id == null) { "Id should be null for new entities" }
            }

            isFalse(tagRepository.existsByLabel(it.label)) { "Tag with label ${tag.label} already exists" }
        }
        .flatMap(tagRepository::save)

    fun deleteTag(tagId: String): Mono<Unit> = tagRepository.deleteById(tagId).toUnit()
}
