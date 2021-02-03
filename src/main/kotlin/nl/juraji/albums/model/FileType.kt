package nl.juraji.albums.model

import org.springframework.http.MediaType

enum class FileType(val contentType: String, val typeName: String, val mediaType: MediaType) {
    JPEG("image/jpeg", "jpeg", MediaType.IMAGE_JPEG),
    BMP("image/bmp", "bmp", MediaType("image", "bmp")),
    GIF("image/gif", "gif", MediaType.IMAGE_GIF),
    PNG("image/png", "png", MediaType.IMAGE_PNG),
    TIFF("image/tiff", "tiff", MediaType("image", "tiff")),
    UNKNOWN("*/*", "", MediaType.ALL);

    companion object {
        fun of(contentType: String): FileType? = values()
            .firstOrNull { pType -> pType.contentType == contentType }

        fun of(mediaType: MediaType): FileType? = values()
            .firstOrNull { pType -> pType.mediaType == mediaType }
    }
}
