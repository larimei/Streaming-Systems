package event

import write.MovingItem

interface EventStore {
    fun saveEvent(event: MovingItemEvent)
    fun loadNamesOfMovingItems(): Map<String, MovingItem>
}