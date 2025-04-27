package cz.chrastecky.kidsmemorygame.theme_provider.provider

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.helper.asFloat
import cz.chrastecky.kidsmemorygame.helper.asInt
import cz.chrastecky.kidsmemorygame.helper.cropY
import cz.chrastecky.kidsmemorygame.helper.rotate
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeDetail
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeMascot
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider

class LocalAssetsThemeProvider(
    private val assetManager: AssetManager,
) : ThemeProvider {
    private val mapper = jacksonObjectMapper()

    override suspend fun listAvailableThemes(): List<ThemeInfo> {
        assetManager.open("themes/themes.json").use { input ->
            val rawList: List<Map<String, String>> = mapper.readValue(input)

            return rawList.map { entry ->
                val id = entry["id"]!!
                val iconPath = entry["icon"]!!

                val bitmap = assetManager.open("themes/$id/$iconPath").use {
                    BitmapFactory.decodeStream(it)
                }

                ThemeInfo(id = id, name =  entry["name"]!!, icon = bitmap)
            }
        }
    }

    override suspend fun ensureThemeDownloaded(id: String): Boolean {
        return true
    }

    override suspend fun getThemeDetail(id: String): ThemeDetail {
        val basePath = "themes/$id"
        assetManager.open("$basePath/theme.json").use { input ->
            val raw: Map<String, Any> = mapper.readValue(input)

            val iconPath = raw["icon"]!!
            val backgroundPath = raw["background"]!!

            val icon = assetManager.open("$basePath/$iconPath").use {
                BitmapFactory.decodeStream(it)
            }
            val cards = (raw["cards"]!! as List<*>).map { cardPath ->
                assetManager.open("$basePath/$cardPath").use {
                    BitmapFactory.decodeStream(it)
                }
            }
            val background = assetManager.open("$basePath/$backgroundPath").use {
                BitmapFactory.decodeStream(it)
            }

            @Suppress("UNCHECKED_CAST")
            val rawMascots = raw["mascots"] as List<Map<String, Any>>

            val mascots = rawMascots.map {
                val rotation = it["rotation"].asFloat()
                val y = it["y"].asInt()

                val path = basePath + "/" + it["image"]
                val mascotImg = assetManager.open(path).use { res ->
                    BitmapFactory.decodeStream(res)
                }.rotate(rotation).cropY(y)

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
    }
}

