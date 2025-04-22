package cz.chrastecky.kidsmemorygame

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    }
}