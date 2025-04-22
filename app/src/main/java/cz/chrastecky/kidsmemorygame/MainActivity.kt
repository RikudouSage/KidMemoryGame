package cz.chrastecky.kidsmemorygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.theme_provider.provider.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.ui.screen.ErrorScreen
import cz.chrastecky.kidsmemorygame.ui.screen.SplashScreen
import cz.chrastecky.kidsmemorygame.ui.screen.ThemePickerScreen
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