package cz.chrastecky.kidsmemorygame.theme_provider.provider

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
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
}
