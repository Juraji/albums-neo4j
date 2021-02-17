package nl.juraji.albums.domain.pictures

enum class FileType(val contentType: String, vararg val typeNames: String) {
    JPEG("image/jpeg", "jpg", "jpeg"),
    BMP("image/bmp", "bmp"),
    GIF("image/gif", "gif"),
    PNG("image/png", "png"),
    TIFF("image/tiff", "tiff"),
    UNKNOWN("*/*");

    companion object {
        fun of(contentType: String): FileType? = values()
            .firstOrNull { pType -> pType.contentType == contentType }

        fun supportsExtension(extension: String): Boolean = values()
            .any { pType -> pType.typeNames.any { tn -> tn.equals(extension, true) } }
    }
}
