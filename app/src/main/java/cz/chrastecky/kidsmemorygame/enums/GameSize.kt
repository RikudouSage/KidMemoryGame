package cz.chrastecky.kidsmemorygame.enums

import cz.chrastecky.kidsmemorygame.enums.contract.HasColumns
import cz.chrastecky.kidsmemorygame.enums.contract.HasRows

enum class GameSize : HasColumns, HasRows {
    Size2x2 {
        override fun columns(): UInt = 2u
        override fun rows(): UInt = 2u
    },
    Size3x2 {
        override fun columns(): UInt = 3u
        override fun rows(): UInt = 2u
    },
    Size4x3 {
        override fun columns(): UInt = 4u
        override fun rows(): UInt = 3u
    },
}