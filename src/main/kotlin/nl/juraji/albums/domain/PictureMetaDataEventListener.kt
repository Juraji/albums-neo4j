package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.filter.GrayscaleFilter
import nl.juraji.albums.configuration.DuplicateScannerConfiguration
import nl.juraji.albums.domain.directories.DirectoryRepository
import nl.juraji.albums.domain.events.ReactiveEventListenerService
import nl.juraji.albums.domain.pictures.*
import nl.juraji.albums.util.toLocalDateTime
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.ValidationException
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import java.awt.Color
import java.awt.Dimension
import java.util.*

@Component
class PictureMetaDataEventListener(
    private val fileOperations: FileOperations,
    private val directoryRepository: DirectoryRepository,
    private val pictureRepository: PictureRepository,
    private val hashDataRepository: HashDataRepository,
    private val configuration: DuplicateScannerConfiguration
) : ReactiveEventListenerService() {

    @EventListener
    fun addPictureToDirectory(event: PictureCreatedEvent) = handleAsMono {
        val parentLocation = event.picture.location.toPath().parent.toString()
        directoryRepository.findByLocation(parentLocation)
            .flatMap { d -> directoryRepository.addPicture(d.id!!, event.picture.id!!) }
    }

    @EventListener
    fun updatePictureMetaData(event: PictureCreatedEvent) = handleAsMono {
        val path = event.picture.location.toPath()

        val fileAttributesMono = fileOperations.readAttributes(path)
        val imageDimensionsMono = fileOperations.loadImage(path)
            .map { Dimension(it.width, it.height) }
        val fileTypeMono = fileOperations
            .readContentType(path)
            .flatMap { FileType.of(it).toMono() }
            .switchIfEmpty { ValidationException("File type is not supported").toMono() }

        Mono.zip(fileAttributesMono, imageDimensionsMono, fileTypeMono)
            .map { (fileAttributes, imageDimensions, fileType) ->
                event.picture.copy(
                    width = imageDimensions.width,
                    height = imageDimensions.height,
                    fileType = fileType,
                    fileSize = fileAttributes.size(),
                    lastModified = fileAttributes.lastModifiedTime().toLocalDateTime()
                )
            }
            .flatMap(pictureRepository::save)
    }

    @EventListener
    fun generatePictureImageHash(event: PictureCreatedEvent) = handleAsMono {
        fileOperations.loadImage(event.picture.location.toPath())
            .flatMap { generateHashBytes(it) }
            .flatMap { hashDataRepository.save(HashData(hash = it)) }
            .flatMap { hashDataRepository.setPictureHashData(event.picture.id!!, it.id!!) }
    }

    private fun generateHashBytes(image: ImmutableImage): Mono<String> = Mono.just(image)
        .map { img ->
            img
                .autocrop(Color.WHITE).autocrop(Color.BLACK)
                .cover(
                    configuration.hashSampleSize,
                    configuration.hashSampleSize,
                    ScaleMethod.Lanczos3,
                    Position.Center
                )
                .filter(GrayscaleFilter())
        }
        .map { sample ->
            val pixels = sample.pixels().map { it.average() }.toTypedArray()
            val offsetPixels = pixels.copyOfRange(1, pixels.lastIndex)

            val bytes = pixels.foldIndexed(BitSet(configuration.hashSize)) { idx, acc, pix ->
                if (idx < offsetPixels.size) {
                    acc.set(idx, pix > offsetPixels[idx])
                }
                acc
            }.toByteArray()

            Base64.getEncoder().encodeToString(bytes)
        }
}
