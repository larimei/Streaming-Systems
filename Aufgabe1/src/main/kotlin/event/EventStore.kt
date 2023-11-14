package event

interface EventStore {
    fun saveEvent(event: MovingItemEvent)
    fun getAllEvents(): List<MovingItemEvent>
}