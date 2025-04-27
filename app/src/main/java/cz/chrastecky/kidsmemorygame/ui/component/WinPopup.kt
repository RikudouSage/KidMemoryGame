package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.TextOnBackgroundColor

@Composable
fun WinPopup(
    onNewGame: () -> Unit,
    onChangeSize: () -> Unit,
    onThemePicker: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)), // dim background
        contentAlignment = Alignment.Center
    ) {
        ConfettiOverlay()
        Box(contentAlignment = Alignment.TopCenter) {
            // Card
            Card(
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .background(CardBackground)
                        .border(width = 6.dp, color = BackgroundColor, shape = RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp)) // Clip BOTH background and content!
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.win_dialog_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextOnBackgroundColor,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.win_dialog_content),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextOnBackgroundColor,
                        )
                    }
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 32.dp), // Protrude buttons
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconCircleButton(Icons.Default.Refresh, "New Game", onNewGame)
                IconCircleButton(Icons.Default.GridView, "Grid Size", onChangeSize)
                IconCircleButton(Icons.Default.Palette, "Themes", onThemePicker)
            }
        }
    }
}