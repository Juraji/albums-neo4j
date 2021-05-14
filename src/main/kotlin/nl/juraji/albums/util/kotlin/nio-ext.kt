package nl.juraji.albums.util.kotlin

import java.nio.file.Path

fun String.toPath(): Path = Path.of(this)
