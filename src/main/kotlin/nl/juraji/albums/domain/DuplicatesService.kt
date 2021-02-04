package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.domain.relationships.DuplicatedByPicture
import nl.juraji.albums.util.toUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime

@Service
class DuplicatesService(
    private val pictureRepository: PictureRepository
) {

    fun markDuplicatePicture(sourceId: String, targetId: String, similarity: Double): Mono<Picture> = Mono
        .zip(
            pictureRepository.findById(sourceId),
            pictureRepository.findById(targetId),
        )
        .map { (source, target) ->
            val relationship = DuplicatedByPicture(target, LocalDateTime.now(), similarity)
            source.copy(duplicates = source.duplicates + relationship)
        }
        .flatMap(pictureRepository::save)

    fun removeDuplicateFromPicture(pictureId: String, targetId: String): Mono<Unit> = pictureRepository
        .removeDuplicatedBy(pictureId, targetId).toUnit()
}
