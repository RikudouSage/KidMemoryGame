package cz.chrastecky.kidsmemorygame.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.enums.GameSize

@Composable
fun GameSizeMiniature(
    background: Bitmap,
    gameSize: GameSize,
    width: Dp,
    onClick: () -> Unit,
) {
    val columns = gameSize.columns().toInt()
    val rows = gameSize.rows().toInt()

    val cardSpacing = 6.dp / columns
    val cardCornerRadius = 4.dp
    val cardBackgroundColor = Color.Black

    Box(
        modifier = Modifier
            .size(width)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
    ) {
        Image(
            bitmap = background.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
                .clip(RoundedCornerShape(16.dp))
        )

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(width / 6)
        ) {
            val maxCardWidth = (maxWidth - (cardSpacing * (columns - 1))) / columns
            val maxCardHeight = (maxHeight - (cardSpacing * (rows - 1))) / rows
            val cardSize = minOf(maxCardWidth, maxCardHeight)

            val gridHeight = (cardSize + cardSpacing) * rows - cardSpacing
            val gridWidth = (cardSize + cardSpacing) * columns - cardSpacing

            Box(
                modifier = Modifier
                    .size(width = gridWidth, height = gridHeight),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(cardSpacing),
                    horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rows * columns) {
                        Box(
                            modifier = Modifier
                                .size(cardSize)
                                .clip(RoundedCornerShape(cardCornerRadius))
                                .background(cardBackgroundColor)
                        )
                    }
                }
            }
        }
    }
}