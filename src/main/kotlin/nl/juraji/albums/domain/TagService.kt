package nl.juraji.albums.domain

import nl.juraji.albums.domain.tags.Tag
import nl.juraji.albums.domain.tags.TagRepository
import nl.juraji.albums.util.mapToUnit
import nl.juraji.reactor.validations.validateAsync
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    fun getAllTags(): Flux<Tag> = tagRepository.findAll()

    fun createTag(label: String, color: String, textColor: String): Mono<Tag> = Mono.just(label)
        .validateAsync {
            isFalse(tagRepository.existsByLabel(it)) { "Tag with label $it already exists" }
        }
        .map { Tag(label = it, color = color, textColor = textColor) }
        .flatMap(tagRepository::save)

    fun deleteTag(tagId: String): Mono<Unit> = tagRepository.deleteById(tagId).mapToUnit()
}
