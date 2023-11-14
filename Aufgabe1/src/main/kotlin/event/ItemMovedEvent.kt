package event

import Vector

data class ItemMovedEvent(override val id: String, val vector: Vector): MovingItemEvent