package read

import Vector
import event.*

class QueryHandler(private val queryModel: MutableMap<String, MovingItemDTO>) : Query {

    override fun getMovingItemByName(name: String): MovingItemDTO {
        return queryModel[name] ?: throw NoSuchElementException("No MovingItem found with name: $name")
    }

    override fun getMovingItems(): List<MovingItemDTO> {
        return queryModel.values.toList()
    }

    override fun getMovingItemsAtPosition(position: Vector): List<MovingItemDTO> {
        return queryModel.values.filter { dto ->
            dto.location == position
        }
    }
}