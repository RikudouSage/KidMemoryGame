package cz.chrastecky.kidsmemorygame.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.enums.GameSize

@Composable
fun ChangeSizePopup(
    background: Bitmap,
    totalCardsAmount: UInt,
    minDistinctImages: UInt,
    onClickOutside: () -> Unit,
    onGameSizeSelected: (GameSize) -> Unit,
) {
    Popup(
        title = stringResource(R.string.change_grid_size_title),
        bodyText = stringResource(R.string.change_grid_size_content),
        onClickOutside = onClickOutside,
        showDimmer = false,
    ) {
        val availableSizes = GameSize.entries.filter { size ->
            val requiredItemsAmount = (size.columns() * size.rows()) / 2u
            totalCardsAmount >= requiredItemsAmount && requiredItemsAmount >= minDistinctImages
        }
        val itemsPerRow = 3
        val itemSize = 80.dp
        val spacing = 16.dp

        BoxWithConstraints {
            val gridWidth = remember(maxWidth) {
                (itemSize * itemsPerRow) + (spacing * (itemsPerRow - 1))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(itemsPerRow),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .width(gridWidth)
            ) {
                items(availableSizes) { size ->
                    GameSizeMiniature(
                        background = background,
                        gameSize = size,
                        width = itemSize,
                        onClick = { onGameSizeSelected(size) }
                    )
                }
            }
        }
    }
}
