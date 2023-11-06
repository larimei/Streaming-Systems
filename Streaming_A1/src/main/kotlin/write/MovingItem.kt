package write

import Vector

interface MovingItem {
    fun getName(): String
    fun getLocation(): Vector
    fun getNumberOfMoves(): Int
    fun getValue(): Int
}