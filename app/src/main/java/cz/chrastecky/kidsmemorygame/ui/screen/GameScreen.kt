package cz.chrastecky.kidsmemorygame.ui.screen

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.enums.GameSize
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeDetail
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.component.GameCard
import cz.chrastecky.kidsmemorygame.ui.dto.GameCardData
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor

@Composable
fun GameScreen(
    themeId: String,
    sharedPreferences: SharedPreferences,
    themeProvider: ThemeProvider,
) {
    var theme by remember { mutableStateOf<ThemeDetail?>(null) }
    var background by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(themeId) {
        sharedPreferences.edit {
            putString("last_theme_id", themeId)
        }
    }

    when {
        theme == null -> GameScreenLoader(themeId, themeProvider) { themeDetail, bitmap ->
            theme = themeDetail
            background = bitmap
        }

        else -> GameScreenMain(
            theme = theme!!,
            background = background!!,
            sharedPreferences = sharedPreferences,
        )
    }
}

@Composable
fun GameScreenLoader(
    themeId: String,
    themeProvider: ThemeProvider,
    onLoaded: (ThemeDetail, Bitmap) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(themeId) {
        val theme = themeProvider.getThemeDetail(themeId)
        val background = BitmapFactory.decodeResource(context.resources, R.drawable.card_back)
        onLoaded(theme, background)
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
fun GameScreenMain(
    theme: ThemeDetail,
    background: Bitmap,
    sharedPreferences: SharedPreferences,
) {
    var cards by remember { mutableStateOf<List<GameCardData>>(emptyList()) }
    val gameSize = remember {
        val storedSize = sharedPreferences.getString("last_used_size", GameSize.Size4x3.name)!!
        try {
            GameSize.valueOf(storedSize)
        } catch (e: IllegalArgumentException) {
            GameSize.Size4x3
        }
    }

    val columns = gameSize.columns().toInt()
    val rows = gameSize.rows().toInt()
    val cardCount = (columns * rows) / 2

    LaunchedEffect(theme.id, cardCount) {
        val selectedImages = theme.cards.shuffled().subList(0, cardCount)
        val mapped = selectedImages.mapIndexed { index, image ->
            GameCardData(
                image = image,
                background = background,
                id = index
            )
        }
        cards = (mapped + mapped).shuffled()
    }

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

        BoxWithConstraints(
            modifier = Modifier
                .padding(horizontal = 100.dp, vertical = 40.dp)
                .fillMaxSize()
        ) {
            val maxCardWidth = maxWidth / columns - 16.dp
            val maxCardHeight = maxHeight / rows - 16.dp
            val cardSize = minOf(maxCardWidth, maxCardHeight)

            val spacing = 16.dp
            val gridWidth = (cardSize + spacing) * columns - spacing

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.width(gridWidth)
                ) {
                    items(cards) { card ->
                        GameCard(card, cardSize) {

                        }
                    }
                }
            }
        }
    }
}