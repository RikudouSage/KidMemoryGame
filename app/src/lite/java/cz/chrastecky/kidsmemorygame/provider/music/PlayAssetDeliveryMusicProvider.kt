package cz.chrastecky.kidsmemorygame.provider.music

import android.content.Context
import cz.chrastecky.kidsmemorygame.dto.MusicTrack
import cz.chrastecky.kidsmemorygame.provider.MusicProvider

// dump class to make stuff compile
class PlayAssetDeliveryMusicProvider(
    context: Context
) : MusicProvider {
    override suspend fun getMusicFiles(): List<MusicTrack> {
        TODO("Not yet implemented")
    }
}