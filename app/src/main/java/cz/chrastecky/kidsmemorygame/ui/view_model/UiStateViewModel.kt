package cz.chrastecky.kidsmemorygame.ui.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UiStateViewModel : ViewModel() {
    private val _reloadKey = MutableStateFlow(0)
    val reloadKey: StateFlow<Int> = _reloadKey

    fun incrementReloadKey() {
        _reloadKey.value++
    }
}