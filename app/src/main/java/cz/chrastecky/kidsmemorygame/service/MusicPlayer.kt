package cz.chrastecky.kidsmemorygame.service

import android.media.MediaPlayer
import cz.chrastecky.kidsmemorygame.dto.MusicTrack

class MusicPlayer {
    private var currentIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private var started = false

    private var playlist: List<MusicTrack> = emptyList()

    fun start(musicFiles: List<MusicTrack>, shuffle: Boolean = true) {
        playlist = if (shuffle) {
            musicFiles.shuffled()
        } else {
            musicFiles
        }

        if (playlist.isEmpty()) {
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
                }
                start(playlist, false)
            }
            prepare()
            start()
        }

        started = true
    }

    fun pause() {
        if (playlist.isEmpty() || mediaPlayer == null) {
            return
        }

        mediaPlayer!!.pause()
    }

    fun resume() {
        if (playlist.isEmpty() || mediaPlayer == null) {
            return
        }

        mediaPlayer!!.start()
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        started = false
    }

    fun isStarted(): Boolean {
        return started
    }
}