package event

import Vector
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ItemMovedEvent")
data class ItemMovedEvent(override val id: String, override val timestamp: Long, val vector: Vector): MovingItemEvent