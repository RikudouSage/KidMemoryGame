package cz.chrastecky.kidsmemorygame.provider.theme

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import cz.chrastecky.kidsmemorygame.dto.ThemeDetail
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.dto.ThemeMascot
import cz.chrastecky.kidsmemorygame.helper.asFloat
import cz.chrastecky.kidsmemorygame.helper.asInt
import cz.chrastecky.kidsmemorygame.helper.cropY
import cz.chrastecky.kidsmemorygame.helper.rotate
import cz.chrastecky.kidsmemorygame.helper.waitForPackIfNeeded
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class PlayAssetDeliveryThemeProvider(
    context: Context,
) : ThemeProvider {
    private val assetPackManager = AssetPackManagerFactory.getInstance(context)
    private val assetManager: AssetManager = context.assets
    private val mapper = jacksonObjectMapper()

    override suspend fun listAvailableThemes(): List<ThemeInfo> {
        val iconsBasePath = waitForPackIfNeeded(assetPackManager, "theme_icons")

        assetManager.open("themes/themes.json").use { input ->
            val rawList: List<Map<String, String>> = mapper.readValue(input)

            return rawList.map { entry ->
                val id = entry["id"]!!
                val iconExtension = File(entry["icon"]!!).extension
                val iconPath = File(iconsBasePath, "$id.$iconExtension")

                val bitmap = BitmapFactory.decodeFile(iconPath.path)

                ThemeInfo(id = id, name =  entry["name"]!!, icon = bitmap)
            }
        }
    }

    override suspend fun isThemeDownloaded(id: String): Boolean {
        val location = assetPackManager.getPackLocation(id)
        return location != null
    }

    override suspend fun getThemeDetail(id: String): ThemeDetail {
        val basePath = waitForPackIfNeeded(assetPackManager, id) + "/$id"

        val input = File(basePath, "theme.json").inputStream()
        val raw: Map<String, Any> = mapper.readValue(input)

        val iconPath = raw["icon"]!!
        val backgroundPath = raw["background"]!!

        val icon = BitmapFactory.decodeFile("$basePath/$iconPath")
        val cards = (raw["cards"]!! as List<*>).map { cardPath -> BitmapFactory.decodeFile("$basePath/$cardPath") }
        val background = BitmapFactory.decodeFile("$basePath/$backgroundPath")

        @Suppress("UNCHECKED_CAST")
        val rawMascots = raw["mascots"] as List<Map<String, Any>>

        val mascots = rawMascots.map {
            val rotation = it["rotation"].asFloat()
            val y = it["y"].asInt()

            val path = basePath + "/" + it["image"]
            val mascotImg = BitmapFactory.decodeFile(path).rotate(rotation).cropY(y)

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
        val existingLocation = assetPackManager.getPackLocation(id)
        if (existingLocation != null) {
            onProgress(1.0f)
            return
        }

        val deferred = CompletableDeferred<Unit>()

        val listener = AssetPackStateUpdateListener { state ->
            if (state.name() != id) {
                return@AssetPackStateUpdateListener
            }

            when (state.status()) {
                AssetPackStatus.PENDING,
                AssetPackStatus.DOWNLOADING,
                AssetPackStatus.TRANSFERRING -> {
                    val progress = state.bytesDownloaded().toFloat() / state.totalBytesToDownload().coerceAtLeast(1L)
                    onProgress(progress)
                }

                AssetPackStatus.COMPLETED -> {
                    onProgress(1.0f)
                    deferred.complete(Unit)
                }

                AssetPackStatus.FAILED,
                AssetPackStatus.CANCELED -> {
                    deferred.completeExceptionally(
                        RuntimeException("Failed to download '$id': status=${state.status()}")
                    )
                }

                AssetPackStatus.NOT_INSTALLED -> {
                    // This can happen if delivery failed without a failure state.
                    deferred.completeExceptionally(
                        RuntimeException("Pack '$id' is not installed after download attempt.")
                    )
                }

                else -> {
                    // Ignored: WAITING_FOR_WIFI, etc.
                }
            }
        }

        assetPackManager.registerListener(listener)

        try {
            assetPackManager.fetch(listOf(id))
            deferred.await()
        } finally {
            assetPackManager.unregisterListener(listener)
        }
    }
}