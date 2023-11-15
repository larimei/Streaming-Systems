package projection

import event.*
import read.MovingItemDTO

class ProjectionHandler(private val eventStore: EventStore, private val queryModel: MutableMap<String, MovingItemDTO>) {
    fun projectEvents() {
        val allEvents = eventStore.getAllEvents()
        allEvents.forEach { event ->
            when (event) {
                is ItemCreatedEvent -> handleCreatedEvent(event)
                is ItemMovedEvent -> handleMovedEvent(event)
                is ItemValueChangedEvent -> handleValueChangedEvent(event)
                is ItemDeletedEvent -> handleDeletedEvent(event)
            }
        }
    }

    private fun handleCreatedEvent(event: ItemCreatedEvent) {
        val dto = MovingItemDTO(event.id, event.position, 0, event.value)
        queryModel[event.id] = dto
    }

    private fun handleMovedEvent(event: ItemMovedEvent) {
        queryModel[event.id]?.let { dto ->
            val newLocation = dto.location.add(event.vector)
            queryModel[event.id] = dto.copy(location = newLocation, numberOfMoves = dto.numberOfMoves + 1)
        }
    }

    private fun handleValueChangedEvent(event: ItemValueChangedEvent) {
        queryModel[event.id]?.let { dto ->
            queryModel[event.id] = dto.copy(value = event.value)
        }
    }

    private fun handleDeletedEvent(event: ItemDeletedEvent) {
        queryModel.remove(event.id)
    }
}