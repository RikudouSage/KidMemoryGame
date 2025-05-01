package cz.chrastecky.kidsmemorygame.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor

@Composable
fun DownloadScreen(
    themeId: String,
    themeProvider: ThemeProvider,
    onDownloaded: () -> Unit,
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(themeId) {
        themeProvider.download(themeId) {
            progress = it
        }

        onDownloaded()
    }

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

        val insets = WindowInsets.safeDrawing.asPaddingValues()
        val left = maxOf(insets.calculateLeftPadding(LocalLayoutDirection.current), 100.dp)
        val right = maxOf(insets.calculateRightPadding(LocalLayoutDirection.current), 100.dp)

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = left, end = right)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}