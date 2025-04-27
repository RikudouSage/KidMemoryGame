package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeMascot
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.ResetAnimationSpeed
import cz.chrastecky.kidsmemorygame.ui.theme.TextOnBackgroundColor
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Popup(
    mascot: ThemeMascot?,
    onNewGame: () -> Unit,
    onChangeSize: () -> Unit,
    onThemePicker: () -> Unit,
    showConfetti: Boolean,
    title: String,
    content: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)), // dim background
        contentAlignment = Alignment.Center
    ) {
        if (showConfetti) {
            ConfettiOverlay()
        }
        Box(contentAlignment = Alignment.TopCenter) {
            if (mascot != null) {
                Mascot(mascot)
            }
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
                            text = title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextOnBackgroundColor,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = content,
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

@Composable
private fun Mascot(
    mascot: ThemeMascot,
) {
    // unrotated width / target size
    val nominal = 150.dp.value / 1024f

    val width = (mascot.image.width * nominal).dp
    val height = (mascot.image.height * nominal).dp

    val min = (-110).dp
    val max = 110.dp

    val upPosition = -height
    val downPosition = 0.dp

    var targetY by remember { mutableStateOf(downPosition) }
    var targetX by remember { mutableStateOf(0.dp) }

    val y by animateDpAsState(
        targetValue = targetY,
        animationSpec = tween(durationMillis = ResetAnimationSpeed)
    )

    LaunchedEffect(Unit) {
        targetY = upPosition
        delay(3000)
        targetY = downPosition

        while (true) {
            delay(3000)
            targetX = (Random.nextFloat() * (max.value - min.value) + min.value).dp
            targetY = upPosition
            delay(3000)
            targetY = downPosition
        }
    }

    Image(
        bitmap = mascot.image.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .size(width, height)
            .offset { IntOffset(0, y.roundToPx()) }
            .offset { IntOffset(targetX.roundToPx(), 0) }
    )
}