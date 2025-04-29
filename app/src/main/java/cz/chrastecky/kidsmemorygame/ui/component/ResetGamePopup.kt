package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R

@Composable
fun ResetGamePopup(
    onNewGame: () -> Unit,
    onChangeSize: () -> Unit,
    onThemePicker: () -> Unit,
    onClickOutside: () -> Unit,
) {
    Popup(
        mascot = null,
        showConfetti = false,
        title = stringResource(R.string.settings),
        bodyText = stringResource(R.string.settings_description),
        onClickOutside = onClickOutside,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 32.dp), // Protrude buttons
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircleButton(Icons.Default.Refresh, "New Game", onClick = onNewGame)
            IconCircleButton(Icons.Default.GridView, "Grid Size", onClick = onChangeSize)
            IconCircleButton(Icons.Default.Palette, "Themes", onClick = onThemePicker)
        }
    }
}