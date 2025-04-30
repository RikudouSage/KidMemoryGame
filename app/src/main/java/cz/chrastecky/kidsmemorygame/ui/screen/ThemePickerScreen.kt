package cz.chrastecky.kidsmemorygame.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.component.GameCard
import cz.chrastecky.kidsmemorygame.ui.component.ThemeCard
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import kotlin.math.ceil

@Composable
fun ThemePickerScreen(
    themes: List<ThemeInfo>,
    themeProvider: ThemeProvider,
    onThemeSelected: (ThemeInfo, Boolean) -> Unit
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val columns = minOf(4, themes.size)
            val spacing = 8.dp
            val itemSize = 120.dp

            val rows = ceil(themes.size / columns.toFloat()).toInt()

            val gridWidth = (itemSize + spacing) * columns - spacing
            val gridHeight = (itemSize + spacing) * rows - spacing

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier
                        .width(gridWidth)
                        .height(gridHeight)
                ) {
                    items(themes) { theme ->
                        ThemeCard(
                            theme = theme,
                            themeProvider = themeProvider,
                        ) { isDownloaded ->
                            onThemeSelected(theme, isDownloaded)
                        }
                    }
                }
            }
        }
    }
}