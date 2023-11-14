package write

import Vector
import write.command.ChangeValueCommand
import write.command.CreateItemCommand
import write.command.DeleteItemCommand
import write.command.MoveItemCommand

class CommandImpl(private val commandHandler: CommandHandler) : Commands {
    override fun createItem(id: String) {
        commandHandler.handle(CreateItemCommand(id))
    }

    override fun createItem(id: String, position: Vector, value: Int) {
        commandHandler.handle(CreateItemCommand(id, position, value))
    }

    override fun deleteItem(id: String) {
        commandHandler.handle(DeleteItemCommand(id))
    }

    override fun moveItem(id: String, vector: Vector) {
        commandHandler.handle(MoveItemCommand(id, vector))
    }

    override fun changeValue(id: String, newValue: Int) {
        commandHandler.handle(ChangeValueCommand(id, newValue))
    }
}