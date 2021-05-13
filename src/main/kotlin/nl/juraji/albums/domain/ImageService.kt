package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.filter.GrayscaleFilter
import com.sksamuel.scrimage.nio.*
import nl.juraji.albums.configuration.ImageServiceConfiguration
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.util.deferTo
import nl.juraji.albums.util.toPath
import org.springframework.core.io.Resource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class ImageService(
    private val configuration: ImageServiceConfiguration
) {
    private val ioScheduler: Scheduler = Schedulers
        .newBoundedElastic(
            10,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "nio-file-operations",
            30,
            true
        )

    fun loadPartAsImage(file: Part): Mono<ImmutableImage> = with(file) { DataBufferUtils.join(content()) }
        .map(DataBuffer::asInputStream)
        .map { ImmutableImage.loader().fromStream(it) }

    fun loadResourceAsImage(resource: Resource): Mono<ImmutableImage> = Mono.just(resource)
        .map { ImmutableImage.loader().fromStream(it.inputStream) }

    fun getThumbnailPath(id: String): Path =
        configuration.thumbnailsDirectory.toPath().resolve(id)

    fun getFullImagePath(id: String): Path =
        configuration.fullImageDirectory.toPath().resolve(id)

    fun saveThumbnail(image: ImmutableImage, id: String): Mono<SavedPicture> = deferTo(ioScheduler) {
        val thumbnailImage = image.cover(configuration.thumbnailSize, configuration.thumbnailSize)

        val path = getThumbnailPath(id)
        val writer = JpegWriter()
            .withCompression(50)
            .withProgressive(true)

        with(writer) {
            Files.createDirectories(path.parent)
            thumbnailImage.output(this, path)
        }

        SavedPicture(path.toString(), 0)
    }

    fun saveFullImage(image: ImmutableImage, id: String, type: FileType): Mono<SavedPicture> = deferTo(ioScheduler) {
        val path = getFullImagePath(id)

        with(writerForFileType(type)) {
            Files.createDirectories(path.parent)
            image.output(this, path)
        }

        SavedPicture(path.toString(), Files.size(path))
    }

    fun generateHash(image: ImmutableImage): Mono<BitSet> = deferTo(ioScheduler) {
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
        val offsetPixels = arrayOf(0) + pixels

        pixels.foldIndexed(BitSet(configuration.hashSize)) { idx, acc, pix ->
            acc.set(idx, pix > offsetPixels[idx])
            acc
        }
    }

    fun deleteThumbnailAndFullImageById(id: String): Mono<Void> = deferTo(ioScheduler) {
        Files.deleteIfExists(getThumbnailPath(id))
        Files.deleteIfExists(getFullImagePath(id))
        null
    }

    private fun writerForFileType(fileType: FileType): ImageWriter = when (fileType) {
        FileType.JPEG -> JpegWriter.NoCompression
        FileType.BMP -> BmpWriter()
        FileType.GIF -> GifWriter.Default
        FileType.PNG -> PngWriter.NoCompression
        FileType.TIFF -> TiffWriter()
        FileType.UNKNOWN -> throw IllegalArgumentException("Unknown file type")
    }

    data class SavedPicture(
        val location: String,
        val filesSize: Long,
    )
}
