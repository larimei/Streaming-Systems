package write.aggregate

import Vector

data class MovingItemAggregate(val id: String, val location: Vector, val value: Int)