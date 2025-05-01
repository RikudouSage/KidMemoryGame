package cz.chrastecky.kidsmemorygame.provider

import cz.chrastecky.kidsmemorygame.dto.ThemeDetail
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo

interface ThemeProvider {
    suspend fun listAvailableThemes(): List<ThemeInfo>
    suspend fun isThemeDownloaded(id: String): Boolean
    suspend fun getThemeDetail(id: String): ThemeDetail
    suspend fun download(id: String, onProgress: (Float) -> Unit)
}
