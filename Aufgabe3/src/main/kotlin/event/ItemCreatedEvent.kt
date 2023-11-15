package event

import Vector
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ItemCreatedEvent")
data class ItemCreatedEvent(
    override val id: String,
    override val timestamp: Long,
    val position: Vector,
    val value: Int
): MovingItemEvent