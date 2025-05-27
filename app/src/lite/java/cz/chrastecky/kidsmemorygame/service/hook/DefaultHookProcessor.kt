package cz.chrastecky.kidsmemorygame.service.hook

import android.content.Context

class DefaultHookProcessor (context: Context) : HookProcessor {
    override fun onGameWon() {
        // do nothing
    }
}