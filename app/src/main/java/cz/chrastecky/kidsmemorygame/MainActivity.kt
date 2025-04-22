package cz.chrastecky.kidsmemorygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.theme_provider.provider.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.KidsMemoryGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var themeProvider: ThemeProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        themeProvider = when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsThemeProvider(assets)
            else -> throw IllegalStateException("Unknown flavor: ${BuildConfig.FLAVOR}")
        }

        enableEdgeToEdge()
        setContent {
            KidsMemoryGameTheme {
                var themeList by remember { mutableStateOf<List<ThemeInfo>?>(null) }
                var loadError by remember { mutableStateOf<Throwable?>(null) }
                var showSplash by remember { mutableStateOf(true) }
                var reloadKey by remember { mutableIntStateOf(0) }

                LaunchedEffect(reloadKey) {
                    val startTime = System.currentTimeMillis()

                    try {
                        val themes = themeProvider.listAvailableThemes()
                        val elapsed = System.currentTimeMillis() - startTime
                        val remainingDelay = 1500L - elapsed
                        if (remainingDelay > 0) {
                            delay(remainingDelay)
                        }

                        themeList = themes
                        showSplash = false
                    } catch (e: Exception) {
                        loadError = e
                        showSplash = false
                    }
                }

                Crossfade (targetState = Triple(showSplash, themeList, loadError)) { (splash, themes, error) ->
                    when {
                        splash -> SplashScreen()

                        error != null -> ErrorScreen(error) {
                            themeList = null
                            loadError = null
                            showSplash = true
                            reloadKey++
                        }

                        themes != null -> ThemePickerScreen(themes) {
                            Log.d("ThemePicker", "Selected: ${it.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
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

@Composable
fun ErrorScreen(error: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.error_prefix, error.message ?: "Unknown error"),
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.button_retry))
            }
        }
    }
}

@Composable
fun ThemePickerScreen(
    themes: List<ThemeInfo>,
    onThemeSelected: (ThemeInfo) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_menu_bg),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            contentDescription = null,
        )
    }
}