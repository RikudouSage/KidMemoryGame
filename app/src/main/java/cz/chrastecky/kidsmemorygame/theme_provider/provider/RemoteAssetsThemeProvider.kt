package cz.chrastecky.kidsmemorygame.theme_provider.provider

import android.content.Context
import android.graphics.BitmapFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.exception.ThemeLoadingFailedException
import cz.chrastecky.kidsmemorygame.helper.asFloat
import cz.chrastecky.kidsmemorygame.helper.asInt
import cz.chrastecky.kidsmemorygame.helper.cropY
import cz.chrastecky.kidsmemorygame.helper.rotate
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeDetail
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeMascot
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import java.io.File

class RemoteAssetsThemeProvider(
    context: Context,
) : ThemeProvider {
    private val baseUrl = "https://raw.githubusercontent.com/RikudouSage/KidMemoryGame/refs/heads/master/themes"

    private val client = HttpClient(CIO)
    private val mapper = jacksonObjectMapper()
    private val themesDir = File(context.filesDir, "themes")

    override suspend fun listAvailableThemes(): List<ThemeInfo> {
        val themesJsonFile = themesDir.resolve("themes.json")
        val jsonString: String
        if (!themesJsonFile.exists()) {
            val themesJsonUrl = "$baseUrl/themes.json"
            val response: HttpResponse = client.get(themesJsonUrl)
            if (!response.status.isSuccess()) {
                throw ThemeLoadingFailedException("Failed loading the list of themes, try again later.")
            }
            jsonString = response.bodyAsText()

            themesJsonFile.parentFile?.mkdirs()
            themesJsonFile.writeText(jsonString)
        } else {
            jsonString = themesJsonFile.readText()
        }

        val rawList: List<Map<String, String>> = mapper.readValue(jsonString)

        return rawList.map { entry ->
            val id = entry["id"]!!
            val iconPath = entry["icon"]!!
            val name = entry["name"]!!

            val iconFile = themesDir.resolve("$id/$iconPath")
            if (!iconFile.exists()) {
                iconFile.parentFile?.mkdirs()
                val iconUrl = "$baseUrl/$id/$iconPath"
                val response = client.get(iconUrl)
                if (!response.status.isSuccess()) {
                    throw ThemeLoadingFailedException("Failed loading a theme icon, try again later")
                }
                val iconBytes = response.body<ByteArray>()
                iconFile.writeBytes(iconBytes)
            }
            val bitmap = iconFile.inputStream().use { BitmapFactory.decodeStream(it) }

            ThemeInfo(id = id, name = name, icon = bitmap)
        }
    }

    override suspend fun isThemeDownloaded(id: String): Boolean {
        val themeFile = themesDir.resolve("$id/theme.json")
        if (!themeFile.exists()) {
            return false
        }

        val raw: Map<String, Any> = mapper.readValue(themeFile.readText())
        @Suppress("UNCHECKED_CAST")
        val cards = (raw["cards"]!! as List<String>)
        cards.forEach {
            val card = themesDir.resolve("$id/$it")
            if (!card.exists()) {
                return false
            }
        }

        val backgroundPath = raw["background"] as String
        val background = themesDir.resolve("$id/$backgroundPath")
        if (!background.exists()) {
            return false
        }

        return true
    }

    override suspend fun getThemeDetail(id: String): ThemeDetail {
        val basePath = themesDir.resolve(id)
        val themeFile = basePath.resolve("theme.json")
        if (!themeFile.exists()) {
            throw ThemeLoadingFailedException("Trying to load a theme that hasn't been downloaded")
        }

        val raw: Map<String, Any> = mapper.readValue(themeFile)

        val iconPath = basePath.resolve(raw["icon"]!! as String)
        val backgroundPath = basePath.resolve(raw["background"]!! as String)

        val icon = BitmapFactory.decodeStream(iconPath.inputStream())
        val cards = (raw["cards"]!! as List<*>).map { cardPath ->
            val cardFile = basePath.resolve(cardPath as String)
            BitmapFactory.decodeStream(cardFile.inputStream())
        }
        val background = BitmapFactory.decodeStream(backgroundPath.inputStream())

        @Suppress("UNCHECKED_CAST")
        val rawMascots = raw["mascots"] as List<Map<String, Any>>

        val mascots = rawMascots.map {
            val rotation = it["rotation"].asFloat()
            val y = it["y"].asInt()

            val path = basePath.resolve(it["image"] as String)
            val mascotImg = BitmapFactory.decodeStream(path.inputStream()).rotate(rotation).cropY(y)

            ThemeMascot(
                image = mascotImg,
                rotation = rotation,
                y = y,
            )
        }

        return ThemeDetail(
            id = raw["id"]!! as String,
            name = raw["name"]!! as String,
            background = background,
            cards = cards,
            icon = icon,
            mascots = mascots,
        )
    }

    override suspend fun download(id: String, onProgress: (Float) -> Unit) {
        val themeFile = themesDir.resolve("$id/theme.json")
        val themeJsonString: String
        if (!themeFile.exists()) {
            val response = client.get("$baseUrl/$id/theme.json")
            if (!response.status.isSuccess()) {
                throw ThemeLoadingFailedException("Failed downloading the theme, try again later")
            }
            themeJsonString = response.bodyAsText()
            themeFile.parentFile?.mkdirs()
            themeFile.writeText(themeJsonString)
        } else {
            themeJsonString = themeFile.readText()
        }

        val raw: Map<String, Any> = mapper.readValue(themeJsonString)

        @Suppress("UNCHECKED_CAST")
        val cards = (raw["cards"]!! as List<String>)
        val totalCards = cards.size.toFloat() + 2f

        onProgress(1f / totalCards)

        cards.forEachIndexed { index, path ->
            val card = themesDir.resolve("$id/$path")
            if (!card.exists()) {
                val cardUrl = "$baseUrl/$id/$path"
                val response = client.get(cardUrl)
                if (!response.status.isSuccess()) {
                    throw ThemeLoadingFailedException("Failed downloading the theme, try again later")
                }
                val bytes = response.body<ByteArray>()
                card.parentFile?.mkdirs()
                card.writeBytes(bytes)
            }

            onProgress((index + 2).toFloat() / totalCards)
        }

        val backgroundPath = raw["background"] as String
        val backgroundFile = themesDir.resolve("$id/$backgroundPath")
        if (!backgroundFile.exists()) {
            val response = client.get("$baseUrl/$id/$backgroundPath")
            if (!response.status.isSuccess()) {
                throw ThemeLoadingFailedException("Failed downloading the theme, try again later")
            }
            val bytes = response.body<ByteArray>()
            backgroundFile.parentFile?.mkdirs()
            backgroundFile.writeBytes(bytes)
        }

        onProgress(1f)
    }
}