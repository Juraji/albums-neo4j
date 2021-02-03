package nl.juraji.albums.util

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneId

fun String.toPath(): Path = Paths.get(this)

fun FileTime.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    LocalDateTime.ofInstant(this.toInstant(), zoneId)
