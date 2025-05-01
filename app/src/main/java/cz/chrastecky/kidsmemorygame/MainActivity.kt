package cz.chrastecky.kidsmemorygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cz.chrastecky.kidsmemorygame.provider.MusicProvider
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.provider.music.LocalAssetsMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.NullMusicProvider
import cz.chrastecky.kidsmemorygame.provider.theme.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.RemoteAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.ui.nav.AppNavigation
import cz.chrastecky.kidsmemorygame.ui.theme.KidsMemoryGameTheme

class MainActivity : ComponentActivity() {
    private lateinit var themeProvider: ThemeProvider
    private lateinit var musicProvider: MusicProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        initialize()

        val sharedPreferences = getSharedPreferences("main", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            KidsMemoryGameTheme {
                AppNavigation(themeProvider, musicProvider, sharedPreferences)
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

    private fun initialize() {
        themeProvider = when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsThemeProvider(assets)
            "lite" -> RemoteAssetsThemeProvider(this)
            else -> throw IllegalStateException("Unknown flavor: ${BuildConfig.FLAVOR}")
        }

        musicProvider = when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsMusicProvider(assets)
            else -> NullMusicProvider()
        }
    }
}