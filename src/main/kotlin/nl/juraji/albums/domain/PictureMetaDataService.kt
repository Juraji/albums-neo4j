package nl.juraji.albums.domain

//@Service
//class PictureMetaDataService(
//    private val pictureRepository: PictureRepository,
//    private val fileOperations: FileOperations,
//    private val applicationEventPublisher: ApplicationEventPublisher
//) {
//
//    fun updateMetaData(pictureId: String): Mono<Picture> = pictureRepository
//        .findById(pictureId)
//        .flatMap { picture ->
//            val path = picture.location.toPath()
//
//            val fileAttributesMono = fileOperations.readAttributes(path)
//            val imageDimensionsMono = fileOperations.loadImage(path)
//                .map { Dimension(it.width, it.height) }
//            val fileTypeMono = fileOperations
//                .readContentType(path)
//                .flatMap { FileType.of(it).toMono() }
//                .switchIfEmpty { ValidationException("File type is not supported").toMono() }
//
//            Mono.zip(fileAttributesMono, imageDimensionsMono, fileTypeMono)
//                .map { (fileAttributes, imageDimensions, fileType) ->
//                    picture.copy(
//                        width = imageDimensions.width,
//                        height = imageDimensions.height,
//                        fileType = fileType,
//                        fileSize = fileAttributes.size,
//                        lastModified = fileAttributes.lastModifiedTime
//                    )
//                }
//        }
//        .flatMap(pictureRepository::save)
//        .doOnNext { applicationEventPublisher.publishEvent(PictureUpdatedEvent(pictureId, it.directory.id)) }
//}
