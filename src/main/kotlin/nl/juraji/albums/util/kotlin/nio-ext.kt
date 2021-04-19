package nl.juraji.albums.util

import java.nio.file.Path

fun String.toPath(): Path = Path.of(this)
