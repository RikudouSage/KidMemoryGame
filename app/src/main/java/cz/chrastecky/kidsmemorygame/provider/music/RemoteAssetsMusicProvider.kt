package cz.chrastecky.kidsmemorygame.provider.music

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.dto.MusicTrack
import cz.chrastecky.kidsmemorygame.provider.MusicProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import java.io.File

class RemoteAssetsMusicProvider(
    context: Context,
) : MusicProvider {
    private val baseUrl = "https://raw.githubusercontent.com/RikudouSage/KidMemoryGame/refs/heads/master/music"

    private val client = HttpClient(CIO)
    private val mapper = jacksonObjectMapper()
    private val musicDir = File(context.filesDir, "music")

    override suspend fun getMusicFiles(): List<MusicTrack> {
        val musicJsonFile = musicDir.resolve("music.json")
        val jsonString: String
        if (!musicJsonFile.exists()) {
            val response: HttpResponse
            try {
                response = client.get("$baseUrl/music.json")
            } catch (e: Throwable) {
                Log.e("DownloadError", e.stackTraceToString())
                return emptyList()
            }
            if (!response.status.isSuccess()) {
                return emptyList()
            }
            jsonString = response.bodyAsText()

            musicJsonFile.parentFile?.mkdirs()
            musicJsonFile.writeText(jsonString)
        } else {
            jsonString = musicJsonFile.readText()
        }

        val trackList: List<String> = mapper.readValue(jsonString)

        return trackList.map {
            val localPath = musicDir.resolve(it)
            if (!localPath.exists()) {
                val response: HttpResponse
                try {
                    response = client.get("$baseUrl/$it")
                } catch (e: Throwable) {
                    Log.e("DownloadError", e.stackTraceToString())
                    return emptyList()
                }
                if (!response.status.isSuccess()) {
                    return emptyList()
                }

                localPath.parentFile?.mkdirs()
                localPath.writeBytes(response.body<ByteArray>())
            }

            val input = ParcelFileDescriptor.open(localPath, ParcelFileDescriptor.MODE_READ_ONLY)
            MusicTrack(
                fileDescriptor = input.fileDescriptor,
                offset = 0,
                length = localPath.length(),
                close = { input.close() }
            )
        }
    }
}