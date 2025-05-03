package cz.chrastecky.kidsmemorygame.provider.music

import android.content.Context
import android.os.ParcelFileDescriptor
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import cz.chrastecky.kidsmemorygame.dto.MusicTrack
import cz.chrastecky.kidsmemorygame.helper.waitForPackIfNeeded
import cz.chrastecky.kidsmemorygame.provider.MusicProvider
import java.io.File

class PlayAssetDeliveryMusicProvider(
    context: Context
) : MusicProvider {
    private val assetPackManager = AssetPackManagerFactory.getInstance(context)
    private val mapper = jacksonObjectMapper()

    override suspend fun getMusicFiles(): List<MusicTrack> {
        val path = waitForPackIfNeeded(assetPackManager, "sound_pack")

        val json: List<String> = mapper.readValue(File(path, "music/music.json").inputStream())
        return json.map {
            val localPath = File("$path/music/$it")
            val input = ParcelFileDescriptor.open(localPath, ParcelFileDescriptor.MODE_READ_ONLY)

            MusicTrack(
                fileDescriptor = input.fileDescriptor,
                offset = 0,
                length = localPath.length(),
                close = { input.close() },
            )
        }
    }
}