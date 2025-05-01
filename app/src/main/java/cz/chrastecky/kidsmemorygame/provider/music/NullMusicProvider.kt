package cz.chrastecky.kidsmemorygame.provider.music

import cz.chrastecky.kidsmemorygame.dto.MusicTrack
import cz.chrastecky.kidsmemorygame.provider.MusicProvider

class NullMusicProvider : MusicProvider {
    override suspend fun getMusicFiles(): List<MusicTrack> {
        return emptyList()
    }
}