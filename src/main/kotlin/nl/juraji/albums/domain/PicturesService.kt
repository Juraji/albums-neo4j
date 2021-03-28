package nl.juraji.albums.domain

import nl.juraji.albums.domain.events.PictureAddedEvent
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

@Service
class PicturesService(
    private val picturesRepository: PicturesRepository,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun getFolderPictures(folderId: String): Flux<Picture> = picturesRepository.findAllByFolderId(folderId)

    fun persistNewPicture(folderId: String, file: FilePart): Mono<Picture> = unpackFilePart(file)
        .validate { (_, contentType, filename) ->
            isNotNull(contentType) { "Missing Content-Type header" }
            isNotNull(filename) { "Missing Content-Disposition header or filename is undefined" }
            isTrue(FileType.supports(contentType)) { "Unsupported Content-Type: $contentType" }
        }
        .validateAsync { (_, _, filename) ->
            isFalse(picturesRepository.existsByNameInFolder(folderId, filename!!)) {
                "A file with name $filename already exists in folder with id $folderId"
            }
        }
        .map { (file, contentType, filename) ->
            Triple(
                file,
                FileType.ofContentType(contentType!!),
                filename!!
            )
        }
        .flatMap { (file, fileType, filename) ->
            logger.info("Incoming file: $filename (as $fileType)")

            val image = imageService.loadFilePartAsImage(file).share()
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

    fun getPictureResource(pictureId: String): Mono<Resource> = picturesRepository
        .findById(pictureId)
        .map { FileSystemResource(it.pictureLocation) }

    fun getThumbnailResource(pictureId: String): Mono<Resource> = picturesRepository
        .findById(pictureId)
        .map { FileSystemResource(it.thumbnailLocation) }

    private fun unpackFilePart(file: FilePart): Mono<Triple<FilePart, String?, String?>> = Mono.just(
        Triple(
            file,
            file.headers().contentType?.toString(),
            file.headers().contentDisposition.filename
        )
    )

    companion object : LoggerCompanion(PicturesService::class)
}
