package event

import Vector

data class ItemCreatedEvent(override val id: String, val position: Vector, val value: Int): MovingItemEvent