package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun ConfettiOverlay() {
    val confettiCount = 30
    val confettiColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)

    BoxWithConstraints (modifier = Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth

        repeat(confettiCount) {
            val offsetY = remember { Animatable(-100f) }
            val offsetX = remember { Random.nextInt(0, screenWidth) }

            LaunchedEffect(Unit) {
                offsetY.animateTo(
                    targetValue = 2000f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = (1000..3000).random(), easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX, offsetY.value.toInt()) }
                    .size((6..12).random().dp)
                    .background(confettiColors.random(), shape = CircleShape)
            )
        }
    }
}