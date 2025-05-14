package cz.chrastecky.kidsmemorygame

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cz.chrastecky.kidsmemorygame.provider.MusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.LocalAssetsMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.NullMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.PlayAssetDeliveryMusicProvider
import cz.chrastecky.kidsmemorygame.provider.music.RemoteAssetsMusicProvider
import cz.chrastecky.kidsmemorygame.provider.theme.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.PlayAssetDeliveryThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.RegistrableThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.RemoteAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.provider.theme.parseFromPlugin
import cz.chrastecky.kidsmemorygame.service.MusicPlayer
import cz.chrastecky.kidsmemorygame.ui.nav.AppNavigation
import cz.chrastecky.kidsmemorygame.ui.theme.KidsMemoryGameTheme
import cz.chrastecky.kidsmemorygame.ui.view_model.UiStateViewModel
import kotlinx.coroutines.launch

private const val ACTION_REQUEST_THEMES = BuildConfig.APPLICATION_ID + ".REQUEST_THEME_INFO"
private const val ACTION_RESPONSE_THEMES = BuildConfig.APPLICATION_ID + ".RESPONSE_THEME_INFO"
private const val EXTRA_RESPONSE_URI = BuildConfig.APPLICATION_ID + ".EXTRA_THEME_DIR_URI"

class MainActivity : ComponentActivity() {
    private lateinit var themeProvider: RegistrableThemeProvider
    private lateinit var musicProvider: MusicProvider
    private var pluginReceiver: BroadcastReceiver? = null
    private val musicPlayer = MusicPlayer()
    private lateinit var uiStateViewModel: UiStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        initialize()

        val sharedPreferences = getSharedPreferences("main", MODE_PRIVATE)
        uiStateViewModel = ViewModelProvider(this)[UiStateViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val reloadKey by uiStateViewModel.reloadKey.collectAsState()

            KidsMemoryGameTheme {
                AppNavigation(
                    themeProvider = themeProvider,
                    sharedPreferences = sharedPreferences,
                    reloadKey = reloadKey,
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

        if (pluginReceiver != null) {
            unregisterReceiver(pluginReceiver)
        }
    }

    override fun onResume() {
        super.onResume()
        musicPlayer.resume()
        registerPluginReceiver()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun initialize() {
        themeProvider = RegistrableThemeProvider(when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsThemeProvider(assets)
            "lite" -> RemoteAssetsThemeProvider(this)
            "playstore" -> PlayAssetDeliveryThemeProvider(this)
            else -> throw IllegalStateException("Unknown flavor: ${BuildConfig.FLAVOR}")
        })

        musicProvider = when (BuildConfig.FLAVOR) {
            "full" -> LocalAssetsMusicProvider(assets)
            "lite" -> RemoteAssetsMusicProvider(this)
            "playstore" -> PlayAssetDeliveryMusicProvider(this)
            else -> NullMusicProvider()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pluginReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val uri = intent?.getStringExtra(EXTRA_RESPONSE_URI)?.toUri()
                    if (uri == null) {
                        Log.w("PluginDiscovery", "A plugin with no theme uri responded")
                        return
                    }

                    val details = parseFromPlugin(uri, this@MainActivity)
                    if (details == null) {
                        Log.w("PluginDiscovery", "A plugin with invalid theme configs")
                        return
                    }

                    details.forEach {
                        themeProvider.register(it)
                    }
                    uiStateViewModel.incrementReloadKey()
                }
            }
            askPluginsToReportThemselves()
        }
    }

    private fun askPluginsToReportThemselves() {
        val intent = Intent(ACTION_REQUEST_THEMES)
        val resolveInfos = packageManager.queryBroadcastReceivers(intent, 0)

        for (info in resolveInfos) {
            val targetComponent = ComponentName(info.activityInfo.packageName, info.activityInfo.name)
            val explicitIntent = Intent(ACTION_REQUEST_THEMES).apply {
                component = targetComponent
            }
            sendBroadcast(explicitIntent)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerPluginReceiver() {
        if (pluginReceiver == null) {
            return
        }

        val filter = IntentFilter(ACTION_RESPONSE_THEMES)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(pluginReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(pluginReceiver, filter)
        }
    }
}