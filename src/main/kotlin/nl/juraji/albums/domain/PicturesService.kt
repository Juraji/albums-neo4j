package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.PictureAddedEvent
import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PicturesRepository
import nl.juraji.albums.util.LoggerCompanion
import nl.juraji.reactor.validations.ValidationException
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

@Service
class PicturesService(
    private val picturesRepository: PicturesRepository,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getFolderPictures(folderId: String): Flux<Picture> = picturesRepository.findAllByFolderId(folderId)

    fun persistNewPicture(folderId: String, file: FilePart): Mono<Picture> {
        val contentType = file.headers().contentType?.toString()
            ?: throw ValidationException("Missing Content-Type header")
        val fileType = FileType.ofContentType(contentType)
            ?: throw ValidationException("Unsupported Content-Type: $contentType")
        val fileName = file.headers().contentDisposition.filename
            ?: throw ValidationException("Missing file name in content disposition")

        logger.info("Incoming file: $fileName ($contentType -> $fileType)")

        val image = imageService.loadFilePartAsImage(file).share()
        val savedPicture = image.flatMap { imageService.savePicture(it, fileType) }
        val savedThumbnail = image.flatMap { imageService.saveThumbnail(it) }


        val picture = Mono.zip(image, savedPicture, savedThumbnail)
            .map { (image, savedPicture, savedThumbnail) ->
                Picture(
                    name = fileName,
                    fileSize = savedPicture.filesSize,
                    width = image.width,
                    height = image.height,
                    type = fileType,
                    pictureLocation = savedPicture.location,
                    thumbnailLocation = savedThumbnail.location
                )
            }

        return validateAsync {
            isFalse(picturesRepository.existsByNameInFolder(folderId, fileName)) {
                "A file with name $fileName already exists in folder with id $folderId"
            }
        }
            .flatMap { picture }
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

    companion object : LoggerCompanion(PicturesService::class)
}
