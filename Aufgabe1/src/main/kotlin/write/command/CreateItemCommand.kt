package write.command

import Vector

data class CreateItemCommand(val id: String, val position: Vector = Vector(), val value: Int = 0)