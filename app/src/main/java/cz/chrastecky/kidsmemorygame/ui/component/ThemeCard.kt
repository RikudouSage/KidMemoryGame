package cz.chrastecky.kidsmemorygame.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.CardBorder

@Composable
fun ThemeCard(
    theme: ThemeInfo,
    themeProvider: ThemeProvider,
    onClick: (isDownloaded: Boolean) -> Unit,
) {
    var isDownloaded by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(theme.id) {
        isDownloaded = themeProvider.isThemeDownloaded(theme.id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable{ onClick(isDownloaded ?: false) }
        ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, CardBorder, RoundedCornerShape(16.dp))
                .clickable { onClick(isDownloaded ?: false) }
                .background(BackgroundColor)
                .width(120.dp)
                .height(120.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = theme.icon.asImageBitmap(),
                contentDescription = theme.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                ,
                contentScale = ContentScale.Fit
            )

            if (isDownloaded == false) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}