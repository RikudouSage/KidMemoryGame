package cz.chrastecky.kidsmemorygame.service.hook

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import cz.chrastecky.kidsmemorygame.enums.SHARED_PREFERENCES_DATABASE_NAME

private const val WinCountName = "winCount"
private const val ReviewDialogShownName = "reviewDialogShown"

class DefaultHookProcessor (
    private val context: Activity
) : HookProcessor {
    private val reviewManager = ReviewManagerFactory.create(context)
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_DATABASE_NAME, Context.MODE_PRIVATE)

    override fun onGameWon() {
        val winCount = sharedPreferences.getInt(WinCountName, 0)
        val reviewRequestShown = sharedPreferences.getBoolean(ReviewDialogShownName, false)

        if (reviewRequestShown) {
            return
        }

        if (winCount >= 5) {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val exception = task.exception
                    if (exception is ReviewException) {
                        @ReviewErrorCode val reviewErrorCode = exception.errorCode
                        Log.w("ReviewError", reviewErrorCode.toString())
                    } else {
                        Log.w("ReviewError", "Unknown exception: ${exception?.message}")
                    }

                    return@addOnCompleteListener
                }

                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(context, reviewInfo)
                flow.addOnCompleteListener {
                    sharedPreferences.edit {
                        putBoolean(ReviewDialogShownName, true)
                    }
                }
            }
            return
        }

        sharedPreferences.edit {
            putInt(WinCountName, winCount + 1)
        }
    }
}