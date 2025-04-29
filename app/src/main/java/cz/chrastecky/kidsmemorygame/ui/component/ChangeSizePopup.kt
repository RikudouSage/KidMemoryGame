package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.chrastecky.kidsmemorygame.R

@Composable
fun ChangeSizePopup(
    onClickOutside: () -> Unit,
) {
    Popup(
        title = stringResource(R.string.change_grid_size_title),
        bodyText = stringResource(R.string.change_grid_size_content),
        onClickOutside = onClickOutside,
        showDimmer = false,
    ) {

    }
}