package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.filter.GrayscaleFilter
import nl.juraji.albums.configuration.DuplicateScannerConfiguration
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.mapToUnit
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.ValidationException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import java.awt.Color
import java.awt.Dimension
import java.util.*

@Service
class PictureMetaDataService(
    private val pictureRepository: PictureRepository,
    private val hashDataRepository: HashDataRepository,
    private val fileOperations: FileOperations,
    private val configuration: DuplicateScannerConfiguration,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun updateMetaData(pictureId: String): Mono<Picture> = pictureRepository
        .findById(pictureId)
        .flatMap { picture ->
            val path = picture.location.toPath()

            val fileAttributesMono = fileOperations.readAttributes(path)
            val imageDimensionsMono = fileOperations.loadImage(path)
                .map { Dimension(it.width, it.height) }
            val fileTypeMono = fileOperations
                .readContentType(path)
                .flatMap { FileType.of(it).toMono() }
                .switchIfEmpty { ValidationException("File type is not supported").toMono() }

            Mono.zip(fileAttributesMono, imageDimensionsMono, fileTypeMono)
                .map { (fileAttributes, imageDimensions, fileType) ->
                    picture.copy(
                        width = imageDimensions.width,
                        height = imageDimensions.height,
                        fileType = fileType,
                        fileSize = fileAttributes.size,
                        lastModified = fileAttributes.lastModifiedTime
                    )
                }
        }
        .flatMap(pictureRepository::save)
        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId)) }

    fun updatePictureHash(pictureId: String): Mono<Unit> = pictureRepository
        .findById(pictureId)
        .flatMap { p -> fileOperations.loadImage(p.location.toPath()).map { p to it } }
        .map { (p, img) -> p to generateHash(img) }
        .flatMap { (p, hash) -> hashDataRepository.save(HashData(hash = hash, picture = p)) }
        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId)) }
        .mapToUnit()

    private fun generateHash(image: ImmutableImage): String {
        val sample = image
            .autocrop(Color.WHITE).autocrop(Color.BLACK)
            .cover(
                configuration.hashSampleSize,
                configuration.hashSampleSize,
                ScaleMethod.Lanczos3,
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
