package nl.juraji.albums.domain.pictures

enum class FileType(val contentType: String, vararg val extensions: String) {
    JPEG("image/jpeg", "jpg", "jpeg"),
    BMP("image/bmp", "bmp"),
    GIF("image/gif", "gif"),
    PNG("image/png", "png"),
    TIFF("image/tiff", "tiff"),
    UNKNOWN("*/*");

    companion object {
        fun ofContentType(contentType: String): FileType? = values()
            .firstOrNull { pType -> pType.contentType.equals(contentType, true) }

        fun isExtensionSupported(extension: String): Boolean = values()
            .any { pType -> pType.extensions.any { tn -> tn.equals(extension, true) } }
    }
}
