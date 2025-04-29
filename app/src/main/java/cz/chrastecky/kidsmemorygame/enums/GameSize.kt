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
    Size4x4 {
        override fun columns(): UInt = 4u
        override fun rows(): UInt = 4u
    },
    Size6x4 {
        override fun columns(): UInt = 6u
        override fun rows(): UInt = 4u
    },
    Size6x5 {
        override fun columns(): UInt = 6u
        override fun rows(): UInt = 5u
    },
    Size7x5 {
        override fun columns(): UInt = 7u
        override fun rows(): UInt = 5u
    },
    Size8x5 {
        override fun columns(): UInt = 8u
        override fun rows(): UInt = 5u
    },
    Size8x6 {
        override fun columns(): UInt = 8u
        override fun rows(): UInt = 6u
    }
}