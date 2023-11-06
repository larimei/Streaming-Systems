package event

import java.util.concurrent.BlockingQueue

class EventStore(private val queue: BlockingQueue<MovingItemEvent>) {

    fun saveEvent(event: MovingItemEvent) {
        queue.put(event)
    }
    fun getAllEvents(): List<MovingItemEvent> {
        val events = mutableListOf<MovingItemEvent>()
        queue.drainTo(events)
        return events
    }
}