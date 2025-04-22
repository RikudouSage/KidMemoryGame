package cz.chrastecky.kidsmemorygame.ui.screen

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.edit

@Composable
fun GameScreen(themeId: String, sharedPreferences: SharedPreferences) {
    LaunchedEffect(themeId) {
        sharedPreferences.edit {
            putString("last_theme_id", themeId)
        }
    }
}