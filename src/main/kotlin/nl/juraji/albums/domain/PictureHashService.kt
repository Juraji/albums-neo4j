package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.filter.GrayscaleFilter
import nl.juraji.albums.configuration.DuplicateScannerConfiguration
import nl.juraji.albums.domain.pictures.PictureHash
import nl.juraji.albums.domain.pictures.PictureHashDataRepository
import nl.juraji.albums.domain.pictures.PictureHashUpdatedEvent
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.awt.Color
import java.util.*

@Service
class PictureHashService(
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations,
    private val pictureHashDataRepository: PictureHashDataRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val configuration: DuplicateScannerConfiguration,
) {

    fun updatePictureHash(pictureId: String): Mono<Unit> = pictureRepository
        .findById(pictureId)
        .flatMap { p -> fileOperations.loadImage(p.location.toPath()).map { p to it } }
        .map { (p, img) -> p to generateHash(img) }
        .flatMap { (p, hash) -> pictureHashDataRepository.save(PictureHash(hash = hash, picture = p)) }
        .doOnNext { applicationEventPublisher.publishEvent(PictureHashUpdatedEvent(pictureId)) }
        .mapToUnit()

    private fun generateHash(image: ImmutableImage): String {
        val sample = image
            .autocrop(Color.WHITE)
            .autocrop(Color.BLACK)
            .cover(
                configuration.hashSampleSize,
                configuration.hashSampleSize,
                ScaleMethod.FastScale,
                Position.Center
            )
            .filter(GrayscaleFilter())

        val pixels = sample.pixels().map { it.average() }.toTypedArray()
        val offsetPixels = pixels.copyOfRange(1, pixels.lastIndex)

        val bytes = offsetPixels.foldIndexed(BitSet(configuration.hashSize)) { idx, acc, pix ->
            acc.set(idx, pix > pixels[idx])
            acc
        }.toByteArray()

        return Base64.getEncoder().encodeToString(bytes)
    }
}
