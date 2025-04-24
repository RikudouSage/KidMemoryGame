package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R

@Composable
fun WinDialog(
    onNewGame: () -> Unit,
    onChangeSize: () -> Unit,
    onThemePicker: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {}, // prevent dismissing by tapping outside
        title = {
            Text(stringResource(R.string.win_dialog_title), style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Text(stringResource(R.string.win_dialog_content))
        },
        confirmButton = {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(stringResource(R.string.new_game_button))
                }
//                Button(
//                    onClick = onChangeSize,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp)
//                ) {
//                    Text(stringResource(R.string.grid_size_button))
//                }
//                Button(
//                    onClick = onThemePicker,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(stringResource(R.string.change_theme_button))
//                }
            }
        }
    )
}