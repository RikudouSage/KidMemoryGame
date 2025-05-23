package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.dto.ThemeMascot
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.ResetAnimationSpeed
import cz.chrastecky.kidsmemorygame.ui.theme.TextOnBackgroundColor
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Popup(
    title: String,
    bodyText: String,
    showConfetti: Boolean = false,
    showDimmer: Boolean = true,
    onClickOutside: (() -> Unit) = {},
    mascot: ThemeMascot? = null,
    buttons: (@Composable BoxScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    var boxWidth by remember { mutableIntStateOf(0) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = if (showDimmer) 0.75f else 0f))
            .clickable { onClickOutside() }
    ) {
        if (showConfetti) {
            ConfettiOverlay()
        }
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.onGloballyPositioned {
                boxWidth = it.size.width
            }
        ) {
            if (mascot != null && boxWidth != 0) {
                Mascot(mascot, boxWidth)
            }
            // Card
            Card(
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .background(CardBackground)
                        .border(
                            width = 6.dp,
                            color = BackgroundColor,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clip(RoundedCornerShape(32.dp)) // Clip BOTH background and content!
                ) inner@ {
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
                            text = bodyText,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextOnBackgroundColor,
                        )

                        content?.invoke(this@inner)
                    }
                }
            }

            buttons?.invoke(this)
        }
    }
}

@Composable
private fun Mascot(
    mascot: ThemeMascot,
    parentWidth: Int,
) {
    // unrotated width / target size
    val nominal = 150.dp.value / 1024f
    val density = LocalDensity.current

    val width = (mascot.image.width * nominal).dp
    val height = (mascot.image.height * nominal).dp

    val mascotWidthPx = with(density) { width.toPx() }

    val availableSpace = parentWidth - mascotWidthPx
    val min = -(availableSpace / 2)
    val max = (availableSpace / 2)

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
            targetX = with(density) {
                (Random.nextFloat() * (max - min) + min).toDp()
            }
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