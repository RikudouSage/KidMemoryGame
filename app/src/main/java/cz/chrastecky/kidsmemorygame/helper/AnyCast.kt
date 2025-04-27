package cz.chrastecky.kidsmemorygame.helper

fun Any?.asFloat(): Float {
    return when (val value = this) {
        is Int -> value.toFloat()
        is Float -> value
        else -> error("Unexpected type: ${value?.javaClass}")
    }
}

fun Any?.asInt(): Int {
    return when (val value = this) {
        is Int -> value
        else -> error("Unexpected type: ${value?.javaClass}")
    }
}