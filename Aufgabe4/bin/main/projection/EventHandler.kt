package projection

import MovingItemImpl
import event.*

class EventHandler(private val itemsMap: MutableMap<String, MovingItemImpl>) {
    fun handleEvent(event: MovingItemEvent) {
        when (event) {
            is ItemCreatedEvent -> handleCreatedEvent(event)
            is ItemMovedEvent -> handleMovedEvent(event)
            is ItemValueChangedEvent -> handleValueChangedEvent(event)
            is ItemDeletedEvent -> handleDeletedEvent(event)
        }
    }

    private fun handleCreatedEvent(event: ItemCreatedEvent) {
        val item = MovingItemImpl(event.id, event.position, event.value)
        itemsMap[event.id] = item
    }

    private fun handleMovedEvent(event: ItemMovedEvent) {
        itemsMap[event.id]?.let { item ->
            item.move(event.vector)
        }
    }

    private fun handleValueChangedEvent(event: ItemValueChangedEvent) {
        itemsMap[event.id]?.let { item ->
            itemsMap[event.id] = item.copy(value = event.value)
        }
    }

    private fun handleDeletedEvent(event: ItemDeletedEvent) {
        itemsMap.remove(event.id)
    }
}