package event

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = ItemCreatedEvent::class, name = "ItemCreatedEvent"),
    JsonSubTypes.Type(value = ItemDeletedEvent::class, name = "ItemDeletedEvent"),
    JsonSubTypes.Type(value = ItemMovedEvent::class, name = "ItemMovedEvent"),
    JsonSubTypes.Type(value = ItemValueChangedEvent::class, name = "ItemValueChangedEvent"),
)
interface MovingItemEvent {
    val id: String
}