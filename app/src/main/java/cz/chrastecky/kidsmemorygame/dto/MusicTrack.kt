package cz.chrastecky.kidsmemorygame.dto

import java.io.FileDescriptor

data class MusicTrack(
    val fileDescriptor: FileDescriptor,
    val offset: Long = 0L,
    val length: Long = -1L,
    val close: () -> Unit = {},
)
