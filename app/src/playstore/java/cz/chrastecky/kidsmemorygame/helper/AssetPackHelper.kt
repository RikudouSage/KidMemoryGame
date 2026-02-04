package cz.chrastecky.kidsmemorygame.helper

import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import kotlinx.coroutines.CompletableDeferred

suspend fun waitForPackIfNeeded(assetPackManager: AssetPackManager, packName: String): String {
    val location = assetPackManager.getPackLocation(packName)
    if (location != null) {
        return location.assetsPath()!!
    }

    val deferred = CompletableDeferred<String>()
    val listener = AssetPackStateUpdateListener { state ->
        if (state.name() != packName) {
            return@AssetPackStateUpdateListener
        }
        if (state.status() == AssetPackStatus.COMPLETED) {
            val loc = assetPackManager.getPackLocation(packName)
            if (loc != null) {
                deferred.complete(loc.assetsPath()!!)
            }
        } else if (state.status() == AssetPackStatus.FAILED || state.status() == AssetPackStatus.CANCELED) {
            deferred.completeExceptionally(RuntimeException("Failed to load theme pack $packName"))
        }
    }

    assetPackManager.registerListener(listener)
    assetPackManager.fetch(listOf(packName))

    try {
        return deferred.await()
    } finally {
        assetPackManager.unregisterListener(listener)
    }
}
