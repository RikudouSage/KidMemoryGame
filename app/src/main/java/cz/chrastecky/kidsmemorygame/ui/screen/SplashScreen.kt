package cz.chrastecky.kidsmemorygame.ui.screen

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    sharedPreferences: SharedPreferences,
    loadThemes: suspend () -> List<ThemeInfo>,
    onLoaded: (List<ThemeInfo>) -> Unit,
    onThemeSelected: (String) -> Unit,
    onError: (Throwable) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        try {
            val themes = loadThemes()

            val lastThemeId = sharedPreferences.getString("last_theme_id", null)

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 1500L - elapsed
            if (remaining > 0) {
                delay(remaining)
            }

            if (lastThemeId != null && themes.any { it.id == lastThemeId }) {
                onThemeSelected(lastThemeId)
            } else {
                onLoaded(themes)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
        ,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = null,
            modifier = Modifier.size(256.dp)
        )
    }
}