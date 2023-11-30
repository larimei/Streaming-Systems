package write

import Vector

interface Commands {
    fun createItem(id: String)
    fun createItem(id: String, position: Vector, value: Int)
    fun deleteItem(id: String)
    fun moveItem(id: String, vector: Vector)
    fun changeValue(id: String, newValue: Int)
}