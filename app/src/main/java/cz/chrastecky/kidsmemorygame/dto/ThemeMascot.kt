package cz.chrastecky.kidsmemorygame.dto

import android.graphics.Bitmap

data class ThemeMascot(
    val image: Bitmap,
    val rotation: Float,
    val y: Int,
)
