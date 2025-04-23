package cz.chrastecky.kidsmemorygame.ui.dto

import android.graphics.Bitmap

data class GameCardData(
    val image: Bitmap,
    val background: Bitmap,
    val id: Int,
    val isMatched: Boolean = false,
    val isFlipped: Boolean = false,
)
