package cz.chrastecky.kidsmemorygame.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.component.IconCircleButton
import cz.chrastecky.kidsmemorygame.ui.component.ThemeCard
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.ButtonBackground
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import kotlin.math.ceil
import kotlin.math.max

@Composable
fun ThemePickerScreen(
    themes: List<ThemeInfo>,
    themeProvider: ThemeProvider,
    onThemeSelected: (ThemeInfo, Boolean) -> Unit,
    onParentSettingsRequested: () -> Unit,
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
        ) inner@ {
            val columns = max(1, minOf(4, themes.size))

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

        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getLeft(density, layoutDirection).toDp()
                        },
                        24.dp,
                    ),
                    end = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getRight(density, layoutDirection).toDp()
                        },
                        24.dp,
                    ),
                    top = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getTop(density).toDp()
                        },
                        24.dp,
                    ),
                    bottom = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getBottom(density).toDp()
                        },
                        24.dp,
                    )
                )
        ) {
            IconCircleButton(
                icon = Icons.Default.Settings,
                contentDescription = stringResource(R.string.parent_settings_button),
                size = 32.dp,
                backgroundColor = ButtonBackground.copy(alpha = 0.8f),
                borderColor = Color.Transparent,
                onClick = onParentSettingsRequested,
            )
        }
    }
}
