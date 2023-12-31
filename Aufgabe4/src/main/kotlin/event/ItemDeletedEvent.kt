package event

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ItemDeletedEvent")
data class ItemDeletedEvent(override val id: String, override val timestamp: Long): MovingItemEvent