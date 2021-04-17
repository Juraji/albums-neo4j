package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.PictureAddedEvent
import nl.juraji.albums.domain.folders.FoldersRepository
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PicturesRepository
import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import reactor.kotlin.extra.bool.not

@Service
class PicturesService(
    private val picturesRepository: PicturesRepository,
    private val foldersRepository: FoldersRepository,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getById(pictureId: String): Mono<Picture> = picturesRepository.findById(pictureId)

    fun getFolderPictures(folderId: String): Flux<Picture> = picturesRepository.findAllByFolderId(folderId)

    fun persistNewPicture(folderId: String, file: FilePart): Mono<Picture> {
        return unpackFilePart(file)
            .validate { (_, fileType, filename) ->
                isNotNull(fileType) { "Missing Content-Type header" }
                isNotNull(filename) { "Missing Content-Disposition header or filename is undefined" }
                isFalse(FileType.UNKNOWN == fileType) { "Unsupported Content-Type" }
            }
            .filterWhen { (_, _, filename) -> picturesRepository.existsByNameInFolder(folderId, filename).not() }
            .flatMap { (file, fileType, filename) ->
                logger.info("Incoming file: $filename (as $fileType)")

                val image = imageService.loadPartAsImage(file).share()
                val savedPicture = image.flatMap { imageService.savePicture(it, fileType) }
                val savedThumbnail = image.flatMap { imageService.saveThumbnail(it) }

                Mono.zip(image, savedPicture, savedThumbnail).map { (image, savedPicture, savedThumbnail) ->
                    Picture(
                        name = filename,
                        fileSize = savedPicture.filesSize,
                        width = image.width,
                        height = image.height,
                        type = fileType,
                        pictureLocation = savedPicture.location,
                        thumbnailLocation = savedThumbnail.location
                    )
                }
            }
            .flatMap(picturesRepository::save)
            .flatMap { picturesRepository.addPictureToFolder(folderId, it.id!!) }
            .doOnNext { applicationEventPublisher.publishEvent(PictureAddedEvent(folderId, it)) }
    }

    fun getPictureResource(pictureId: String): Mono<Resource> = picturesRepository
        .findById(pictureId)
        .map { FileSystemResource(it.pictureLocation) }

    fun getThumbnailResource(pictureId: String): Mono<Resource> = picturesRepository
        .findById(pictureId)
        .map { FileSystemResource(it.thumbnailLocation) }

    fun movePicture(pictureId: String, targetFolderId: String): Mono<Picture> = getById(pictureId)
        .validateAsync { picture ->
            isTrue(foldersRepository.existsById(targetFolderId)) { "No folder with id $targetFolderId exists" }
            isFalse(picturesRepository.existsByNameInFolder(targetFolderId, picture.name)) {
                "A file with name ${picture.name} already exists in folder with id $targetFolderId"
            }
        }
        .flatMap { picturesRepository.addPictureToFolder(targetFolderId, it.id!!) }

    fun deletePicture(pictureId: String): Mono<Void> {
        val picture = getById(pictureId).share()
        val deletePicture = picture.flatMap { imageService.deleteByPath(it.pictureLocation) }
        val deleteThumbnail = picture.flatMap { imageService.deleteByPath(it.thumbnailLocation) }
        val deleteFromDb = picture.flatMap(picturesRepository::delete)

        return deletePicture
            .then(deleteThumbnail)
            .then(deleteFromDb)
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
