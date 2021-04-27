package nl.juraji.albums.domain

import com.sksamuel.scrimage.ImageParseException
import nl.juraji.albums.domain.events.FolderDeletedEvent
import nl.juraji.albums.domain.events.PictureAddedEvent
import nl.juraji.albums.domain.events.PictureDeletedEvent
import nl.juraji.albums.domain.events.ReactiveEventListener
import nl.juraji.albums.domain.folders.FoldersRepository
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PicturesRepository
import nl.juraji.albums.util.kotlin.LoggerCompanion
import nl.juraji.reactor.validations.ValidationException
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.extra.bool.not
import java.time.LocalDateTime

@Service
class PicturesService(
    private val picturesRepository: PicturesRepository,
    private val foldersRepository: FoldersRepository,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ReactiveEventListener() {
    fun getById(pictureId: String): Mono<Picture> = picturesRepository.findById(pictureId)

    fun getFolderPictures(folderId: String): Flux<Picture> = picturesRepository.findAllByFolderId(folderId)

    fun persistNewPicture(folderId: String, file: FilePart): Mono<Picture> =
        unpackFilePart(file)
            .validate { (_, fileType, filename) ->
                isNotNull(fileType) { "Missing Content-Type header" }
                isNotNull(filename) { "Missing Content-Disposition header" }
                isFalse(FileType.UNKNOWN == fileType) { "Unsupported Content-Type" }
            }
            .filterWhen { (_, _, filename) -> picturesRepository.existsByNameInFolder(folderId, filename).not() }
            .doOnNext { (_, fileType, filename) -> logger.info("Incoming file: $filename (as $fileType)") }
            .flatMap { (file, fileType, filename) ->
                Mono.zip(
                    picturesRepository.save(Picture(name = filename, type = fileType)),
                    Mono.just(file),
                )
            }
            .flatMap { (picture, file) ->
                val image = imageService.loadPartAsImage(file).share()
                val savedFullImage = image.flatMap { imageService.saveFullImage(it, picture.id!!, picture.type) }
                val savedThumbnail = image.flatMap { imageService.saveThumbnail(it, picture.id!!) }

                Mono.zip(image, savedFullImage, savedThumbnail).map { (image, savedPicture) ->
                    picture.copy(
                        fileSize = savedPicture.filesSize,
                        width = image.width,
                        height = image.height,
                    )
                }
            }
            .flatMap(picturesRepository::save)
            .flatMap { picturesRepository.addPictureToFolder(folderId, it.id!!) }
            .onErrorMap(ImageParseException::class.java) { ValidationException(it.localizedMessage) }
            .doOnNext { applicationEventPublisher.publishEvent(PictureAddedEvent(folderId, it)) }

    fun updatePicture(pictureId: String, update: Picture): Mono<Picture> =
        picturesRepository.findById(pictureId)
            .validateAsync { existing ->
                synchronous {
                    isTrue(update.id == existing.id) { "Picture id in body does not match picture id in parameters" }
                }

                isFalse(
                    foldersRepository.findByPictureId(pictureId).flatMap {
                        picturesRepository.existsByNameInFolder(it.id!!, update.name)
                    }) { "A picture with name ${update.name} already exists in the same folder" }
            }
            .map {
                it.copy(
                    name = update.name,
                    lastModified = LocalDateTime.now()
                )
            }
            .flatMap(picturesRepository::save)

    fun getPictureResource(pictureId: String): Mono<Resource> =
        Mono.just(pictureId).map { FileSystemResource(imageService.getFullImagePath(it)) }

    fun getThumbnailResource(pictureId: String): Mono<Resource> =
        Mono.just(pictureId).map { FileSystemResource(imageService.getThumbnailPath(it)) }

    fun movePicture(pictureId: String, targetFolderId: String): Mono<Picture> =
        getById(pictureId)
            .validateAsync { picture ->
                isTrue(foldersRepository.existsById(targetFolderId)) { "No folder with id $targetFolderId exists" }
                isFalse(picturesRepository.existsByNameInFolder(targetFolderId, picture.name)) {
                    "A file with name ${picture.name} already exists in folder with id $targetFolderId"
                }
            }
            .flatMap { picturesRepository.addPictureToFolder(targetFolderId, it.id!!) }

    fun deletePicture(pictureId: String): Mono<Void> =
        picturesRepository
            .deleteFully(pictureId)
            .doFinally { this.applicationEventPublisher.publishEvent(PictureDeletedEvent(pictureId)) }

    @Async
    @EventListener(FolderDeletedEvent::class)
    fun onFolderDeleted(e: FolderDeletedEvent) = consumePublisher {
        picturesRepository
            .findOrphaned()
            .flatMap { deletePicture(it.id!!) }
    }

    @Async
    @EventListener(PictureDeletedEvent::class)
    fun onPictureDeleted(e: PictureDeletedEvent) = consumePublisher {
        imageService.deleteThumbnailAndFullImageById(e.pictureId)
    }

    private fun unpackFilePart(file: FilePart): Mono<Triple<FilePart, FileType, String>> = Mono.just(
        Triple(
            file,
            file.headers().contentType?.let { FileType.ofContentType(it.toString()) } ?: FileType.UNKNOWN,
            file.headers().contentDisposition.filename!!
        )
    )

    companion object : LoggerCompanion(PicturesService::class)
}
