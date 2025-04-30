package cz.chrastecky.kidsmemorygame.theme_provider

interface ThemeProvider {
    suspend fun listAvailableThemes(): List<ThemeInfo>
    suspend fun isThemeDownloaded(id: String): Boolean
    suspend fun getThemeDetail(id: String): ThemeDetail
    suspend fun download(id: String, onProgress: (Float) -> Unit)
}
