package cz.chrastecky.kidsmemorygame.provider

import android.graphics.Bitmap

data class ThemeDetail(
    val id: String,
    val name: String,
    val background: Bitmap,
    val cards: List<Bitmap>,
    val icon: Bitmap,
    val mascots: List<ThemeMascot>,
)
