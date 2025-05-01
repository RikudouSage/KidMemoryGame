package cz.chrastecky.kidsmemorygame.provider

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThemeInfo(
    val id: String,
    val name: String,
    val icon: Bitmap,
)
