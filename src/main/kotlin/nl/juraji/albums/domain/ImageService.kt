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
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.awt.Color
import java.nio.file.Files
import java.util.*

@Service
class ImageService(
    private val configuration: ImageServiceConfiguration
) {
    private val ioScheduler: Scheduler = Schedulers
        .newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "nio-file-operations",
            30,
            true
        )

    fun loadFilePartAsImage(file: FilePart): Mono<ImmutableImage> = with(file) { DataBufferUtils.join(content()) }
        .map { it.asInputStream() }
        .map { ImmutableImage.loader().fromStream(it) }

    fun saveThumbnail(image: ImmutableImage): Mono<SavedPicture> = deferTo(ioScheduler) {
        val id = UUID.randomUUID().toString()
        val thumbnailImage = image.cover(configuration.thumbnailSize, configuration.thumbnailSize)

        val path = configuration.thumbnailsDirectory.toPath().resolve("$id.jpg")
        val writer = JpegWriter()
            .withCompression(50)
            .withProgressive(true)

        with(writer) {
            thumbnailImage.output(this, path)
        }

        SavedPicture(path.toString(), 0)
    }

    fun savePicture(image: ImmutableImage, fileType: FileType): Mono<SavedPicture> = deferTo(ioScheduler) {
        val id = UUID.randomUUID().toString()
        val path = configuration.picturesDirectory.toPath().resolve("$id.jpg")

        with(writerForFileType(fileType)) {
            image.output(this, path)
        }

        SavedPicture(path.toString(), Files.size(path))
    }

    fun generateHash(image: ImmutableImage): Mono<String> = deferTo(ioScheduler) {
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

        Base64.getEncoder().encodeToString(bytes)
    }

    private fun writerForFileType(fileType: FileType): ImageWriter = when (fileType) {
        FileType.JPEG -> JpegWriter.NoCompression
        FileType.BMP -> BmpWriter()
        FileType.GIF -> GifWriter.Default
        FileType.PNG -> PngWriter.NoCompression
        FileType.TIFF -> TiffWriter()
        else -> throw IllegalArgumentException("No writer defined for file type $fileType")
    }

    data class SavedPicture(
        val location: String,
        val filesSize: Long,
    )
}
