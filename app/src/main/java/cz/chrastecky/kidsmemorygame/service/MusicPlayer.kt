package cz.chrastecky.kidsmemorygame.service

import android.content.Context
import android.media.MediaPlayer
import cz.chrastecky.kidsmemorygame.dto.MusicTrack

class MusicPlayer(
    private val context: Context,
    musicFiles: List<MusicTrack>,
) {
    private var currentIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private val playlist = musicFiles.shuffled().toMutableList()

    fun start() {
        if (playlist.size == 0) {
            return
        }

        val musicFile = playlist[currentIndex]

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(musicFile.fileDescriptor, musicFile.offset, musicFile.length)
            setOnCompletionListener {
                currentIndex++
                if (currentIndex >= playlist.size) {
                    currentIndex = 0
                    playlist.shuffle()
                }
                start()
            }
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}