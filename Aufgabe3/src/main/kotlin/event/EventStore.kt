package event

interface EventStore {
    fun saveEvent(event: MovingItemEvent)
}