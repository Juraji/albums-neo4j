package nl.juraji.albums.domain

import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.albums.util.deferTo
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

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

    fun readAttributes(path: Path): Mono<BasicFileAttributes> = deferTo(scheduler) {
        Files.readAttributes(path, BasicFileAttributes::class.java)
    }

    fun deleteIfExists(path: Path) = deferTo(scheduler) {
        Files.deleteIfExists(path)
    }
    fun getParentPathStr(path: String): String = path.substringBeforeLast(File.separatorChar)

    companion object : LoggerCompanion(FileOperations::class)
}
