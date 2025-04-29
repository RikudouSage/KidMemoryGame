package cz.chrastecky.kidsmemorygame.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.enums.GameSize

@Composable
fun ChangeSizePopup(
    background: Bitmap,
    onClickOutside: () -> Unit,
    onGameSizeSelected: (GameSize) -> Unit,
) {
    Popup(
        title = stringResource(R.string.change_grid_size_title),
        bodyText = stringResource(R.string.change_grid_size_content),
        onClickOutside = onClickOutside,
        showDimmer = false,
    ) {
        val availableSizes = GameSize.entries

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            items(availableSizes) { size ->
                GameSizeMiniature(
                    background = background,
                    gameSize = size,
                    onClick = { onGameSizeSelected(size) }
                )
            }
        }
    }
}