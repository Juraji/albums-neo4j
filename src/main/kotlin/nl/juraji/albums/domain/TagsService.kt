package nl.juraji.albums.domain

import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.domain.tags.TagsRepository
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagsService(
    private val tagsRepository: TagsRepository
) {

    fun getAllTags(): Flux<Tag> = tagsRepository.findAll()

    fun createTag(tag: Tag): Mono<Tag> = Mono.just(tag)
        .validateAsync {
            isFalse(tagsRepository.existsByLabel(it.label)) { "A tag with label ${tag.label} already exists" }
        }
        .flatMap(tagsRepository::save)

    fun updateTag(tagId: String, update: Tag): Mono<Tag> = tagsRepository
        .findById(tagId)
        .validateAsync {
            unless(it.label == update.label) {
                isFalse(tagsRepository.existsByLabel(update.label)) { "A tag with label ${update.label} already exists" }
            }
        }
        .map {
            it.copy(
                label = update.label,
                textColor = update.textColor,
                color = update.color
            )
        }
        .flatMap(tagsRepository::save)

    fun deleteTag(tagId: String): Mono<Void> = tagsRepository
        .deleteById(tagId)
}
