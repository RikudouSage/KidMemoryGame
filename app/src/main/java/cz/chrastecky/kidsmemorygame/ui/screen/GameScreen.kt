package cz.chrastecky.kidsmemorygame.ui.screen

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.enums.GameSize
import cz.chrastecky.kidsmemorygame.enums.SharedPreferenceName
import cz.chrastecky.kidsmemorygame.dto.ThemeDetail
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.component.ChangeSizePopup
import cz.chrastecky.kidsmemorygame.ui.component.GameCard
import cz.chrastecky.kidsmemorygame.ui.component.IconCircleButton
import cz.chrastecky.kidsmemorygame.ui.component.ResetGamePopup
import cz.chrastecky.kidsmemorygame.ui.component.WinPopup
import cz.chrastecky.kidsmemorygame.ui.dto.GameCardData
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.ButtonBackground
import cz.chrastecky.kidsmemorygame.ui.theme.ResetAnimationSpeed
import kotlinx.coroutines.delay

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun GameScreen(
    themeId: String,
    sharedPreferences: SharedPreferences,
    themeProvider: ThemeProvider,
    reloadGameKey: Int,
    onRequestReset: () -> Unit,
    onThemeChangeRequested: () -> Unit,
) {
    var theme by remember { mutableStateOf<ThemeDetail?>(null) }
    var background by remember { mutableStateOf<Bitmap?>(null) }

    Crossfade(
        targetState = theme != null,
        animationSpec = tween(durationMillis = ResetAnimationSpeed),
    ) { loaded ->
        if (!loaded) {
            GameScreenLoader(themeId, themeProvider) { themeDetail, bitmap ->
                background = bitmap
                theme = themeDetail
            }
        } else {
            key(reloadGameKey) {
                GameScreenMain(
                    theme = theme!!,
                    background = background!!,
                    sharedPreferences = sharedPreferences,
                    onRequestReset = onRequestReset,
                    onThemeChangeRequested = onThemeChangeRequested,
                )
            }
        }
    }
}

@Composable
private fun GameScreenLoader(
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
private fun GameScreenMain(
    theme: ThemeDetail,
    background: Bitmap,
    sharedPreferences: SharedPreferences,
    onRequestReset: () -> Unit,
    onThemeChangeRequested: () -> Unit,
) {
    var cards by remember { mutableStateOf<List<GameCardData>>(emptyList()) }
    var flippedCards by remember { mutableStateOf<List<Int>>(emptyList()) }
    val gameSize = remember {
        val default = GameSize.Size4x3
        val storedSize = sharedPreferences.getString(SharedPreferenceName.GameSize.name, default.name)!!

        try {
            var storedSizeEnum = GameSize.valueOf(storedSize)
            val requiredImageCount = (storedSizeEnum.columns() * storedSizeEnum.rows()) / 2u
            if (theme.cards.size.toUInt() < requiredImageCount) {
                storedSizeEnum = GameSize.Size4x3
                sharedPreferences.edit {
                    putString(SharedPreferenceName.GameSize.name, storedSizeEnum.name)
                }
            }

            storedSizeEnum
        } catch (e: IllegalArgumentException) {
            default
        }
    }
    var resetTrigger by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showChangeSizeMenu by remember { mutableStateOf(false) }
    val mascot by remember { mutableStateOf(theme.mascots.shuffled().firstOrNull()) }

    val columns = gameSize.columns().toInt()
    val rows = gameSize.rows().toInt()
    val cardCount = (columns * rows) / 2
    val hasWon = cards.isNotEmpty() && cards.all { it.isMatched }// || true

    LaunchedEffect(theme.id, cardCount) {
        sharedPreferences.edit {
            putString(SharedPreferenceName.LastUsedTheme.name, theme.id)
        }

        val selectedImages = theme.cards.shuffled().subList(0, cardCount)
        val mapped = selectedImages.mapIndexed { index, image ->
            GameCardData(
                image = image,
                background = background,
                imageId = index,
                cardId = 0,
            )
        }
        cards = (mapped + mapped).shuffled().mapIndexed { index, card ->
            card.copy(cardId = index)
        }
    }

    LaunchedEffect(resetTrigger) {
        if (resetTrigger) {
            delay(1000)
            cards = cards.map {
                if (it.cardId in flippedCards) it.copy(isFlipped = false) else it
            }
            flippedCards = emptyList()
            resetTrigger = false
        }
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
            val spacing = 32.dp / columns

            val maxCardWidth = maxWidth / columns - spacing
            val maxCardHeight = maxHeight / rows - spacing
            val cardSize = minOf(maxCardWidth, maxCardHeight)

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

                            if (
                                card.isMatched
                                || (flippedCards.size == 2 && !flippedCards.contains(card.cardId))
                            ) {
                                return@GameCard
                            }

                            val newCards = cards.map {
                                if (it.cardId == card.cardId) it.copy(isFlipped = !it.isFlipped) else it
                            }
                            cards = newCards

                            val isCurrentlyFlipped = cards.first { it.cardId == card.cardId }.isFlipped
                            flippedCards = if (isCurrentlyFlipped) {
                                flippedCards + card.cardId
                            } else {
                                flippedCards - card.cardId
                            }

                            if (flippedCards.size == 2) {
                                val (first, second) = newCards.filter { it.cardId in flippedCards }

                                if (first.imageId == second.imageId) {
                                    cards = newCards.map {
                                        if (it.cardId == first.cardId || it.cardId == second.cardId)
                                            it.copy(isMatched = true, isFlipped = false)
                                        else it
                                    }
                                    flippedCards = emptyList()
                                } else {
                                    resetTrigger = true
                                }
                            }
                        }
                    }
                }
            }
        }

        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getLeft(density, layoutDirection).toDp()
                        },
                        32.dp,
                    ),
                    end = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getRight(density, layoutDirection).toDp()
                        },
                        32.dp,
                    ),
                    top = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getTop(density).toDp()
                        },
                        32.dp,
                    ),
                    bottom = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getBottom(density).toDp()
                        },
                        32.dp,
                    )
                )
        ) {
            IconCircleButton(
                icon = Icons.Default.Sync,
                contentDescription = stringResource(R.string.settings_button),
                size = 32.dp,
                backgroundColor = ButtonBackground.copy(alpha = 0.8f),
                borderColor = Color.Transparent,
            ) {
                showSettingsMenu = true
            }
        }

        fun onNewGame() {
            cards = emptyList()
            onRequestReset()
        }

        if (hasWon) {
            WinPopup(
                mascot = mascot,
                onNewGame = { onNewGame() },
                onChangeSize = {
                    showChangeSizeMenu = true
                },
                onThemePicker = { onThemeChangeRequested() }
            )
        } else if (showSettingsMenu) {
            ResetGamePopup(
                onNewGame = { onNewGame() },
                onChangeSize = {
                    showChangeSizeMenu = true
                },
                onThemePicker = { onThemeChangeRequested() },
                onClickOutside = {showSettingsMenu = false},
            )
        }

        if (showChangeSizeMenu) {
            ChangeSizePopup(
                background = theme.background,
                totalCardsAmount = theme.cards.size.toUInt(),
                onClickOutside = {showChangeSizeMenu = false},
            ) { newSize ->
                showChangeSizeMenu = false

                sharedPreferences.edit {
                    putString(SharedPreferenceName.GameSize.name, newSize.name)
                }
                onRequestReset()
            }
        }
    }
}