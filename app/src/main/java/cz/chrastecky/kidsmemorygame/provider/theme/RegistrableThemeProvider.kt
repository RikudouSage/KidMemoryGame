package cz.chrastecky.kidsmemorygame.provider.theme

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import cz.chrastecky.kidsmemorygame.dto.ThemeDetail
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import androidx.core.net.toUri
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.dto.ThemeMascot
import cz.chrastecky.kidsmemorygame.helper.asFloat
import cz.chrastecky.kidsmemorygame.helper.asInt
import cz.chrastecky.kidsmemorygame.helper.cropY
import cz.chrastecky.kidsmemorygame.helper.rotate

class RegistrableThemeProvider (
    private val themeProvider: ThemeProvider,
) : ThemeProvider {
    private val registeredThemes = mutableListOf<ThemeDetail>()

    override suspend fun listAvailableThemes(): List<ThemeInfo> {
        return registeredThemes.map {
            ThemeInfo(
                id = it.id,
                name = it.name,
                icon = it.icon,
            )
        } + themeProvider.listAvailableThemes()
    }

    override suspend fun isThemeDownloaded(id: String): Boolean {
        if (registeredThemes.map { it.id }.contains(id)) {
            return true
        }

        return themeProvider.isThemeDownloaded(id)
    }

    override suspend fun getThemeDetail(id: String): ThemeDetail {
        if (registeredThemes.map { it.id }.contains(id)) {
            return registeredThemes.first { it.id == id }
        }

        return themeProvider.getThemeDetail(id)
    }

    override suspend fun download(id: String, onProgress: (Float) -> Unit) {
        if (registeredThemes.map { it.id }.contains(id)) {
            onProgress(1f)
            return
        }

        return themeProvider.download(id, onProgress)
    }

    fun register(themeDetail: ThemeDetail) {
        if (registeredThemes.map { it.id }.contains(themeDetail.id)) {
            return
        }

        this.registeredThemes.add(themeDetail)
    }
}

@Suppress("UNCHECKED_CAST")
fun parseFromPlugin(providerUri: Uri, context: Context): List<ThemeDetail>? {
    val mapper = jacksonObjectMapper()
    val cursor = context.contentResolver.query(providerUri, null, null, null, null)

    cursor?.use { it ->
        if (!it.moveToNext()) {
            return null
        }

        val basePath = it.getString(it.getColumnIndexOrThrow("basePath"))
        val packageId = it.getString(it.getColumnIndexOrThrow("packageId"))

        val jsonString = context.contentResolver.openInputStream("$basePath/themes.json".toUri())?.bufferedReader()?.use { it.readText() }
        if (jsonString == null) {
            return null
        }
        val json: List<Map<String, String>> = mapper.readValue(jsonString)

        return json.map { themeInfoJson ->
            val themeDetailJsonPath = "$basePath/${themeInfoJson["configPath"]}"
            val themeDetailJsonString = context.contentResolver.openInputStream(themeDetailJsonPath.toUri())?.bufferedReader()?.use { it.readText() }
            println(themeDetailJsonString)
            if (themeDetailJsonString == null) {
                return null
            }
            val themeDetailJson: Map<String, Any> = mapper.readValue(themeDetailJsonString)

            val iconPath = themeDetailJson["icon"] as String?
            val backgroundPath = themeDetailJson["background"] as String?
            val cardsRaw = themeDetailJson["cards"] as List<String>?
            val rawMascots = themeDetailJson["mascots"] as List<Map<String, Any>>

            if (iconPath == null || backgroundPath == null || cardsRaw == null) {
                return null
            }

            val background = context.contentResolver.openInputStream("$basePath/${themeInfoJson["id"]}/$backgroundPath".toUri())?.use { BitmapFactory.decodeStream(it) }
            val icon = context.contentResolver.openInputStream("$basePath/${themeInfoJson["id"]}/$iconPath".toUri())?.use { BitmapFactory.decodeStream(it) }
            if (background == null || icon == null) {
                return null
            }

            val cards = (themeDetailJson["cards"]!! as List<*>).map { cardPath ->
                val cardBitmap = context.contentResolver.openInputStream("$basePath/${themeInfoJson["id"]}/$cardPath".toUri())?.use { BitmapFactory.decodeStream(it) }
                if (cardBitmap == null) {
                    return null
                }

                cardBitmap
            }
            val mascots = rawMascots.map {
                val rotation = it["rotation"].asFloat()
                val y = it["y"].asInt()

                val path = "$basePath/${themeInfoJson["id"]}/${it["image"]}"
                var mascotImg = context.contentResolver.openInputStream(path.toUri())?.use { input -> BitmapFactory.decodeStream(input) }
                if (mascotImg == null) {
                    return null
                }
                mascotImg = mascotImg.rotate(rotation).cropY(y)

                ThemeMascot(
                    image = mascotImg,
                    rotation = rotation,
                    y = y,
                )
            }

            ThemeDetail(
                id = "$packageId.${themeInfoJson["id"]}",
                name = themeInfoJson["name"]!!,
                background = background,
                cards = cards,
                icon = icon,
                mascots = mascots,
            )
        }
    }
    return null
}