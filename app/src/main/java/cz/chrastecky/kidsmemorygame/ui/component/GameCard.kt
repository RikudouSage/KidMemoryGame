package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.ui.dto.GameCardData
import cz.chrastecky.kidsmemorygame.ui.theme.CardAnimationSpeed
import cz.chrastecky.kidsmemorygame.ui.theme.GameCardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.SuccessCardBorder

@Composable
fun GameCard(
    card: GameCardData,
    size: Dp,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = CardAnimationSpeed.toInt())
    )

    val isFront = rotation > 90f
    val imageBitmap = if (isFront) {
        card.image.asImageBitmap()
    } else {
        card.background.asImageBitmap()
    }
    val glowAlpha by animateFloatAsState(
        targetValue = if (card.isMatched) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
                shadowElevation = if (card.isMatched) 12f else 2f
                shape = RoundedCornerShape(12.dp)
                clip = true
            }
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (card.isMatched) 3.dp else 2.dp,
                color = if (card.isMatched) SuccessCardBorder.copy(alpha = glowAlpha) else Color.White,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(enabled = !card.isMatched) { onClick() }
            .background(GameCardBackground),
        contentAlignment = Alignment.Center
    ) {
        var modifier = Modifier.fillMaxSize()
        if (isFront) {
            modifier = modifier.graphicsLayer {
                rotationY = 180f
            }
        }
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    }
}