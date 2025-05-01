package cz.chrastecky.kidsmemorygame.provider

import cz.chrastecky.kidsmemorygame.dto.MusicTrack

interface MusicProvider {
    suspend fun getMusicFiles(): List<MusicTrack>
}