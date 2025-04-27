package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeMascot

@Composable
fun WinPopup(
    mascot: ThemeMascot,
    onNewGame: () -> Unit,
    onChangeSize: () -> Unit,
    onThemePicker: () -> Unit
) {
    Popup(
        mascot = mascot,
        onNewGame = onNewGame,
        onChangeSize = onChangeSize,
        onThemePicker = onThemePicker,
        showConfetti = true,
        title = stringResource(R.string.win_dialog_title),
        content = stringResource(R.string.win_dialog_content),
    ) {}
}
