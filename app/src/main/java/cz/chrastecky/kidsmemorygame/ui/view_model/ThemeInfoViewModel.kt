package cz.chrastecky.kidsmemorygame.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo

class ThemeInfoViewModel : ViewModel() {
    var themes by mutableStateOf<List<ThemeInfo>?>(null)
}