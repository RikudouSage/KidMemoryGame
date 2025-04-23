package cz.chrastecky.kidsmemorygame.ui.dto

import android.graphics.Bitmap

data class GameCardData(
    val image: Bitmap,
    val background: Bitmap,
    val imageId: Int,
    val cardId: Int,
    val isMatched: Boolean = false,
    val isFlipped: Boolean = false,
)
