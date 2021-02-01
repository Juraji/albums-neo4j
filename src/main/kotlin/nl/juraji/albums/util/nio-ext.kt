package nl.juraji.albums.util

import java.nio.file.Path
import java.nio.file.Paths

fun String.toPath(): Path = Paths.get(this)
