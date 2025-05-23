package cz.chrastecky.kidsmemorygame.ui.screen

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
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    loadThemes: suspend () -> List<ThemeInfo>,
    onLoaded: (List<ThemeInfo>) -> Unit,
    onError: (Throwable) -> Unit = {},
    reloadKey: Int,
) {
    LaunchedEffect(reloadKey) {
        val startTime = System.currentTimeMillis()
        try {
            val themes = loadThemes()

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 500L - elapsed
            if (remaining > 0) {
                delay(remaining)
            }

            onLoaded(themes)
        } catch (e: CancellationException) {
            // ignore
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