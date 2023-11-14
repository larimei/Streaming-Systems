package event


data class ItemValueChangedEvent(override val id: String, val value: Int): MovingItemEvent