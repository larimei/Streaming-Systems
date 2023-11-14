package read

import Vector

data class MovingItemDTO(
    val name: String,
    val location: Vector,
    val numberOfMoves: Int,
    val value: Int
)