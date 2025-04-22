package cz.chrastecky.kidsmemorygame.ui.screen

import android.content.SharedPreferences
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeDetail
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor

@Composable
fun GameScreen(
    themeId: String,
    sharedPreferences: SharedPreferences,
    themeProvider: ThemeProvider,
) {
    var theme by remember { mutableStateOf<ThemeDetail?>(null) }

    LaunchedEffect(themeId) {
        sharedPreferences.edit {
            putString("last_theme_id", themeId)
        }
    }

    when {
        theme == null -> GameScreenLoader(themeId, themeProvider) {
            theme = it
        }

        else -> GameScreenMain(theme!!)
    }
}

@Composable
fun GameScreenLoader(
    themeId: String,
    themeProvider: ThemeProvider,
    onLoaded: (ThemeDetail) -> Unit
) {
    LaunchedEffect(themeId) {
        val theme = themeProvider.getThemeDetail(themeId)
        onLoaded(theme)
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

@Composable
fun GameScreenMain(theme: ThemeDetail) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = BitmapPainter(theme.background.asImageBitmap()),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        Text(text = "// TODO", style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
}