package event

class EventStore {
    private val events = mutableListOf<MovingItemEvent>()

    fun saveEvent(event: MovingItemEvent) {
        events.add(event)
    }

    fun getEventsForAggregate(id: String): List<MovingItemEvent> {
        return events.filter { it.id == id }
    }
}