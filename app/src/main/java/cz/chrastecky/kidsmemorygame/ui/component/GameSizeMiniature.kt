package cz.chrastecky.kidsmemorygame.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.enums.GameSize

@Composable
fun GameSizeMiniature(
    background: Bitmap,
    gameSize: GameSize,
    onClick: () -> Unit,
) {
    val cardSpacing = 2.dp
    val cardCornerRadius = 4.dp
    val cardBackgroundColor = Color.LightGray

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        val columns = gameSize.columns().toInt()
        val rows = gameSize.rows().toInt()

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val maxCardWidth = (maxWidth - (cardSpacing * (columns - 1))) / columns
            val maxCardHeight = (maxHeight - (cardSpacing * (rows - 1))) / rows
            val cardSize = minOf(maxCardWidth, maxCardHeight)

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