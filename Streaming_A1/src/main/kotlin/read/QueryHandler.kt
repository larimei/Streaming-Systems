package read

import event.*
import write.aggregate.MovingItemAggregate

class QueryHandler(private val eventStore: EventStore) {
    private val movingItems = mutableListOf<MovingItemAggregate>() // MovingItemDto

    fun handleEvents(events: List<MovingItemEvent>) {
        for (event in events) {
            when (event) {
                is ItemCreatedEvent -> {
                    movingItems.add(MovingItemAggregate(event.id, event.position, event.value))
                }

                is ItemMovedEvent -> {
                    val movingItemIndex = movingItems.indexOfFirst { it.id == event.id }
                    if (movingItemIndex != -1) {
                        val currentMovingItem = movingItems[movingItemIndex]
                        movingItems[movingItemIndex] = MovingItemAggregate(
                            currentMovingItem.id, currentMovingItem.location.add(event.vector), currentMovingItem.value
                        )
                    }
                }

                is ItemValueChangedEvent -> {
                    val movingItemIndex = movingItems.indexOfFirst { it.id == event.id }
                    if (movingItemIndex != -1) {
                        val currentMovingItem = movingItems[movingItemIndex]
                        movingItems[movingItemIndex] =
                            MovingItemAggregate(currentMovingItem.id, currentMovingItem.location, event.value)
                    }
                }

                is ItemDeletedEvent -> {
                    movingItems.remove(movingItems.find { it.id == event.id })
                }
            }
        }
    }
}