package cz.chrastecky.kidsmemorygame.helper

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotate(angle: Float): Bitmap {
    if (angle == 0f) {
        return Bitmap.createBitmap(this)
    }

    val matrix = Matrix().apply { postRotate(angle) }
    return Bitmap.createBitmap(
        this,
        0, 0, this.width, this.height,
        matrix, true,
    )
}

fun Bitmap.cropY(y: Int): Bitmap {
    return Bitmap.createBitmap(
        this,
        0, 0,
        this.width,
        y
    )
}