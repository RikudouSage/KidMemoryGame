package cz.chrastecky.kidsmemorygame.theme_provider

interface ThemeProvider {
    suspend fun listAvailableThemes(): List<ThemeInfo>
    suspend fun ensureThemeDownloaded(id: String): Boolean
    suspend fun getThemeDetail(id: String): ThemeDetail
}
