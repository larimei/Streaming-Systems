package read
import Vector

interface Query {
    fun getMovingItemByName(name: String): MovingItemDTO
    fun getMovingItems(): List<MovingItemDTO>
    fun getMovingItemsAtPosition(position: Vector): List<MovingItemDTO>
}