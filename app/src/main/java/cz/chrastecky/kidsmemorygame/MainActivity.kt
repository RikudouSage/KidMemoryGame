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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.theme_provider.provider.LocalAssetsThemeProvider
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.CardBorder
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
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.theme_picker_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 180.dp, end = 180.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
            ) {
                items(themes) { theme ->
                    ThemeCard(theme = theme, onClick = { onThemeSelected(theme) })
                }
            }
        }
    }
}

@Composable
fun ThemeCard(theme: ThemeInfo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable{ onClick() }
        ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, CardBorder, RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .background(BackgroundColor)
                .width(120.dp)
                .height(120.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = theme.icon.asImageBitmap(),
                contentDescription = theme.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                ,
                contentScale = ContentScale.Fit
            )
        }
    }
}