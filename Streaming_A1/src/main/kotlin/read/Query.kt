package read
import Vector

interface Query {
    fun getMovingItemByName(name: String): MovingItemDto
    fun getMovingItems(): List<MovingItemDto>
    fun getMovingItemsAtPosition(position: Vector): List<MovingItemDto>
}