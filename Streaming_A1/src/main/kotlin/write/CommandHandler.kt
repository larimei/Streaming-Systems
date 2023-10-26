package write

import event.*
import write.aggregate.MovingItemAggregate
import write.command.ChangeValueCommand
import write.command.CreateItemCommand
import write.command.DeleteItemCommand
import write.command.MoveItemCommand

class CommandHandler(private val eventStore: EventStore) {
    private val movingItems = mutableListOf<MovingItemAggregate>()

    fun handle(createItemCommand: CreateItemCommand) {
        val newMovingItem =
            MovingItemAggregate(createItemCommand.id, createItemCommand.position, createItemCommand.value)
        movingItems.add(newMovingItem)

        eventStore.saveEvent(ItemCreatedEvent(newMovingItem.id, newMovingItem.location, newMovingItem.value))
    }

    fun handle(changeValueCommand: ChangeValueCommand) {
        val movingItemIndex = movingItems.indexOfFirst { it.id == changeValueCommand.id }
        if (movingItemIndex != -1) {
            val currentMovingItem = movingItems[movingItemIndex]
            movingItems[movingItemIndex] =
                MovingItemAggregate(currentMovingItem.id, currentMovingItem.location, changeValueCommand.newValue)

            eventStore.saveEvent(ItemValueChangedEvent(changeValueCommand.id, changeValueCommand.newValue))
        }
    }

    fun handle(deleteItemCommand: DeleteItemCommand) {
        movingItems.remove(movingItems.find { it.id == deleteItemCommand.id })

        eventStore.saveEvent(ItemDeletedEvent(deleteItemCommand.id))
    }

    fun handle(moveItemCommand: MoveItemCommand) {
        val movingItemIndex = movingItems.indexOfFirst { it.id == moveItemCommand.id }
        if (movingItemIndex != -1) {
            val currentMovingItem = movingItems[movingItemIndex]
            movingItems[movingItemIndex] = MovingItemAggregate(
                currentMovingItem.id, currentMovingItem.location.add(moveItemCommand.vector), currentMovingItem.value
            )

            eventStore.saveEvent(ItemMovedEvent(moveItemCommand.id, moveItemCommand.vector))
        }
    }
}