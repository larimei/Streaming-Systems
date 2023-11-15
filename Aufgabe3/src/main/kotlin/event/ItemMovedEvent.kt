package event

import Vector
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ItemMovedEvent")
data class ItemMovedEvent(override val id: String, val vector: Vector): MovingItemEvent