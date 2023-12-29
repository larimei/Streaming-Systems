package write

import MovingItemImpl
import Vector
import event.*
import write.command.ChangeValueCommand
import write.command.CreateItemCommand
import write.command.DeleteItemCommand
import write.command.MoveItemCommand


class CommandHandler(
    private val eventStore: EventStore
) {
    fun handle(createItemCommand: CreateItemCommand) {
        if(createItemCommand.id in eventStore.loadNamesOfMovingItems()) {
            println("Already exists")
            return
        }

        val item = MovingItemImpl(createItemCommand.id, createItemCommand.position, createItemCommand.value)
        eventStore.saveEvent(ItemCreatedEvent(item.getName(), System.currentTimeMillis(), item.getLocation(), item.getValue()))
    }

    fun handle(changeValueCommand: ChangeValueCommand) {
        val item = eventStore.loadNamesOfMovingItems()[changeValueCommand.id]
        if (item == null) {
            println("Item does not exist")
            return
        }
        eventStore.saveEvent(ItemValueChangedEvent(item.getName(), System.currentTimeMillis(), item.getValue()))
    }

    fun handle(deleteItemCommand: DeleteItemCommand) {
        if (deleteItemCommand.id !in eventStore.loadNamesOfMovingItems()) {
            println("Item does not exist")
            return
        }

        eventStore.saveEvent(ItemDeletedEvent(deleteItemCommand.id, System.currentTimeMillis()))
    }

    fun handle(moveItemCommand: MoveItemCommand) {
        val items = eventStore.loadNamesOfMovingItems()
        val item = items[moveItemCommand.id]
        if (item == null) {
            println("Item does not exist")
            return
        }
        if (item.getNumberOfMoves() >= 19) {
            println("Move should be executed for the 20th time - Item will be deleted instead")
            this.handle(DeleteItemCommand(moveItemCommand.id))
            return
        }

        if (moveItemCommand.vector != Vector(0,0,0)) {

            val itemsAtSamePosition = items.filterValues { value ->
                value.getLocation() == item.getLocation().add(moveItemCommand.vector)
            }

            if (itemsAtSamePosition.isNotEmpty()) {
                println("Already one item at this position - will be deleted")
                itemsAtSamePosition.forEach{(key, _) ->
                    this.handle(DeleteItemCommand(key))
                }
            }
        }

        eventStore.saveEvent(ItemMovedEvent(moveItemCommand.id, System.currentTimeMillis(), moveItemCommand.vector))
    }
}