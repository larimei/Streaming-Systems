package write

import MovingItemImpl
import event.*
import write.command.ChangeValueCommand
import write.command.CreateItemCommand
import write.command.DeleteItemCommand
import write.command.MoveItemCommand

class CommandHandler(private val eventStore: EventStore, private val domainItems: MutableMap<String, MovingItemImpl>) {

    fun handle(createItemCommand: CreateItemCommand) {
        if(createItemCommand.id in domainItems) {
            println("Already exists")
            return
        }

        val item = MovingItemImpl(createItemCommand.id, createItemCommand.position, createItemCommand.value)
        domainItems[createItemCommand.id] = item
        eventStore.saveEvent(ItemCreatedEvent(item.getName(), item.getLocation(), item.getValue()))
    }

    fun handle(changeValueCommand: ChangeValueCommand) {
        val item = domainItems[changeValueCommand.id]
        if (item == null) {
            println("Item does not exist")
            return
        }
        item.changeValue(changeValueCommand.newValue)
        domainItems[changeValueCommand.id] = item
        eventStore.saveEvent(ItemValueChangedEvent(item.getName(), item.getValue()))
    }

    fun handle(deleteItemCommand: DeleteItemCommand) {
        if (deleteItemCommand.id !in domainItems) {
            println("Item does not exist")
            return
        }
        domainItems.remove(deleteItemCommand.id)

        eventStore.saveEvent(ItemDeletedEvent(deleteItemCommand.id))
    }

    fun handle(moveItemCommand: MoveItemCommand) {
        val item = domainItems[moveItemCommand.id]
        if (item == null) {
            println("Item does not exist")
            return
        }
        item.move(moveItemCommand.vector)
        domainItems[moveItemCommand.id] = item

        eventStore.saveEvent(ItemMovedEvent(moveItemCommand.id, moveItemCommand.vector))
    }
}