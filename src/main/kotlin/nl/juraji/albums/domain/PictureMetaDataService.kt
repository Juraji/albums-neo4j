package nl.juraji.albums.domain

import nl.juraji.albums.domain.pictures.FileType
import nl.juraji.albums.domain.pictures.Picture
import nl.juraji.albums.domain.pictures.PictureRepository
import nl.juraji.albums.domain.pictures.PictureUpdatedEvent
import nl.juraji.albums.util.toPath
import nl.juraji.reactor.validations.ValidationException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import java.awt.Dimension

@Service
class PictureMetaDataService(
    private val pictureRepository: PictureRepository,
    private val fileOperations: FileOperations,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun updateMetaData(pictureId: String): Mono<Picture> = pictureRepository
        .findById(pictureId)
        .flatMap { picture ->
            val path = picture.location.toPath()

            val fileAttributesMono = fileOperations.readAttributes(path)
            val imageDimensionsMono = fileOperations.loadImage(path)
                .map { Dimension(it.width, it.height) }
            val fileTypeMono = fileOperations
                .readContentType(path)
                .flatMap { FileType.of(it).toMono() }
                .switchIfEmpty { ValidationException("File type is not supported").toMono() }

            Mono.zip(fileAttributesMono, imageDimensionsMono, fileTypeMono)
                .map { (fileAttributes, imageDimensions, fileType) ->
                    picture.copy(
                        width = imageDimensions.width,
                        height = imageDimensions.height,
                        fileType = fileType,
                        fileSize = fileAttributes.size,
                        lastModified = fileAttributes.lastModifiedTime
                    )
                }
        }
        .flatMap(pictureRepository::save)
        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId)) }
}
