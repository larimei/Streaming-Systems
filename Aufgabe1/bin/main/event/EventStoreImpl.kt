package event

import java.util.concurrent.BlockingQueue

class EventStoreImpl(private val queue: BlockingQueue<MovingItemEvent>): EventStore {

    override fun saveEvent(event: MovingItemEvent) {
        queue.put(event)
    }
    override fun getAllEvents(): List<MovingItemEvent> {
        val events = mutableListOf<MovingItemEvent>()
        queue.drainTo(events)
        return events
    }
}