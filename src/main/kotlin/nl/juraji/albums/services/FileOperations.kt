package nl.juraji.albums.services

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path

@Service
class FileOperations(
    @Qualifier("nioFileOperationsScheduler") private val scheduler: Scheduler
) {

    fun exists(path: Path): Mono<Boolean> = Mono
        .fromSupplier { Files.exists(path, LinkOption.NOFOLLOW_LINKS) }
        .subscribeOn(scheduler)

    fun getParentPathStr(path:String): String = path.substringBeforeLast(File.separatorChar)
}
