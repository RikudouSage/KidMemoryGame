package cz.chrastecky.kidsmemorygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import cz.chrastecky.kidsmemorygame.provider.MusicProvider
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.provider.music.LocalAssetsMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.NullMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.RemoteAssetsMusicProvider
import cz.chrastecky.kidsmemorygame.provider.theme.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.RemoteAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.service.MusicPlayer
import cz.chrastecky.kidsmemorygame.ui.nav.AppNavigation
import cz.chrastecky.kidsmemorygame.ui.theme.KidsMemoryGameTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var themeProvider: ThemeProvider
    private lateinit var musicProvider: MusicProvider
    private val musicPlayer = MusicPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        initialize()

        val sharedPreferences = getSharedPreferences("main", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            KidsMemoryGameTheme {
                AppNavigation(
                    themeProvider = themeProvider,
                    sharedPreferences = sharedPreferences,
                )
            }
        }
        hideSystemUI()

        lifecycleScope.launch {
            musicPlayer.start(musicProvider.getMusicFiles())
        }
    }

    override fun onPause() {
        super.onPause()
        musicPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        musicPlayer.resume()
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
            "lite" -> RemoteAssetsMusicProvider(this)
            else -> NullMusicProvider()
        }
    }
}