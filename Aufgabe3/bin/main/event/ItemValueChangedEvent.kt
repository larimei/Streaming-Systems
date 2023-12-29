package event

import com.fasterxml.jackson.annotation.JsonTypeName


@JsonTypeName("ItemValueChangedEvent")
data class ItemValueChangedEvent(override val id: String, override val timestamp: Long, val value: Int): MovingItemEvent