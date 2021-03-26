package nl.juraji.albums.domain.pictures

enum class FileType(val contentType: String) {
    JPEG("image/jpeg"),
    BMP("image/bmp"),
    GIF("image/gif"),
    PNG("image/png"),
    TIFF("image/tiff"),
    UNKNOWN("*/*");

    companion object {
        fun ofContentType(contentType: String): FileType? = values()
            .firstOrNull { pType -> pType.contentType.equals(contentType, true) }
    }
}
