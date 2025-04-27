package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.ButtonBackground

@Composable
fun IconCircleButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    backgroundColor: Color = ButtonBackground,
    borderColor: Color = BackgroundColor,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        tonalElevation = size / 10,
        color = backgroundColor,
        shadowElevation = size / 16,
        modifier = modifier
            .size(size)
            .border(width = size / 10, color = borderColor, shape = CircleShape)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.Black,
                modifier = Modifier.size(size / 2),
            )
        }
    }
}