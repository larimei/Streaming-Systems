package event

import Vector
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ItemCreatedEvent")
data class ItemCreatedEvent(override val id: String, val position: Vector, val value: Int): MovingItemEvent