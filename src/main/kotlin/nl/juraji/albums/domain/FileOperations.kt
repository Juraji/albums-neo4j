package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImmutableImage
import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.albums.util.deferIterableTo
import nl.juraji.albums.util.deferTo
import nl.juraji.albums.util.toLocalDateTime
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime

@Service
class FileOperations {
    private val scheduler: Scheduler = Schedulers
        .newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "nio-file-operations",
            30,
            true
        )

    fun exists(path: Path): Mono<Boolean> = deferTo(scheduler) {
        Files.exists(path, LinkOption.NOFOLLOW_LINKS)
    }

    fun readContentType(path: Path): Mono<String> = deferTo(scheduler) {
        Files.probeContentType(path)
    }

    fun readAttributes(path: Path): Mono<FileAttributes> = deferTo(scheduler) {
        val osAttrs = Files.readAttributes(path, BasicFileAttributes::class.java)

        FileAttributes(
            size = osAttrs.size(),
            isDirectory = osAttrs.isDirectory,
            isRegularFile = osAttrs.isRegularFile,
            lastModifiedTime = osAttrs.lastModifiedTime().toLocalDateTime(),
            lastAccessTime = osAttrs.lastAccessTime().toLocalDateTime(),
            creationTime = osAttrs.creationTime().toLocalDateTime(),
        )
    }

    fun deleteIfExists(path: Path): Mono<Boolean> = deferTo(scheduler) {
        Files.deleteIfExists(path)
    }

    fun loadImage(path: Path): Mono<ImmutableImage> = deferTo(scheduler) {
        ImmutableImage.loader().fromPath(path)
    }

    fun listDirectories(root: Path, recursive: Boolean): Flux<Path> = deferIterableTo(scheduler) {
        val depth = if (recursive) Int.MAX_VALUE else 1
        root.toFile().walkTopDown().maxDepth(depth)
            .filter { it.isDirectory }
            .map { it.toPath() }
            .toList()
    }

    companion object : LoggerCompanion(FileOperations::class)
}

data class FileAttributes(
    val size: Long,
    val isDirectory: Boolean,
    val isRegularFile: Boolean,
    val lastModifiedTime: LocalDateTime,
    val lastAccessTime: LocalDateTime,
    val creationTime: LocalDateTime
)
