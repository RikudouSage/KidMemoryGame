package cz.chrastecky.kidsmemorygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.theme_provider.provider.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.ui.nav.AppNavigation
import cz.chrastecky.kidsmemorygame.ui.theme.KidsMemoryGameTheme

class MainActivity : ComponentActivity() {
    private lateinit var themeProvider: ThemeProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        themeProvider = when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsThemeProvider(assets)
            else -> throw IllegalStateException("Unknown flavor: ${BuildConfig.FLAVOR}")
        }

        val sharedPreferences = getSharedPreferences("main", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            KidsMemoryGameTheme {
                AppNavigation(themeProvider, sharedPreferences)
            }
        }
        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}