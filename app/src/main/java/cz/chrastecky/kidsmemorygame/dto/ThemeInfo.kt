package cz.chrastecky.kidsmemorygame.dto

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThemeInfo(
    val id: String,
    val name: String,
    val icon: Bitmap,
)
