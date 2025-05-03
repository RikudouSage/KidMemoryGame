package cz.chrastecky.kidsmemorygame.provider.theme

import android.content.Context
import cz.chrastecky.kidsmemorygame.dto.ThemeDetail
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider

// dummy class to make stuff compile
class PlayAssetDeliveryThemeProvider(
    context: Context,
) : ThemeProvider {
    override suspend fun listAvailableThemes(): List<ThemeInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun isThemeDownloaded(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getThemeDetail(id: String): ThemeDetail {
        TODO("Not yet implemented")
    }

    override suspend fun download(id: String, onProgress: (Float) -> Unit) {
        TODO("Not yet implemented")
    }

}