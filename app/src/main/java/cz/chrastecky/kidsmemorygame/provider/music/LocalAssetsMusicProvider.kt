package cz.chrastecky.kidsmemorygame.provider.music

import android.content.res.AssetManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.dto.MusicTrack
import cz.chrastecky.kidsmemorygame.provider.MusicProvider

class LocalAssetsMusicProvider(
    private val assetManager: AssetManager,
) : MusicProvider {
    private val mapper = jacksonObjectMapper()

    override suspend fun getMusicFiles(): List<MusicTrack> {
        val json: List<String> = mapper.readValue(assetManager.open("music/music.json"))
        return json.map {
            val fd = assetManager.openFd("music/$it")
            MusicTrack(
                fileDescriptor = fd.fileDescriptor,
                offset = fd.startOffset,
                length = fd.length,
                close = { fd.close() },
            )
        }
    }
}