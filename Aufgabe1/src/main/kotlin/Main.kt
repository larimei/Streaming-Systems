import event.EventStoreImpl
import event.MovingItemEvent
import projection.ProjectionHandler
import read.MovingItemDTO
import read.QueryHandler
import write.CommandHandler
import write.CommandImpl
import java.util.concurrent.LinkedBlockingQueue
import kotlin.random.Random

fun main(args: Array<String>) {
    val eventQueue = LinkedBlockingQueue<MovingItemEvent>()
    val eventStore = EventStoreImpl(eventQueue)
    val domainItems = mutableMapOf<String, MovingItemImpl>()
    val commandHandler = CommandHandler(eventStore, domainItems)
    val commandImpl = CommandImpl(commandHandler)

    for (i in 1..6) {
        commandImpl.createItem(i.toString())
        val randomMoveCount = Random.nextInt(1,5)
        for (j in 1..randomMoveCount) {
            val randomValues = Vector(Random.nextInt(0,5), Random.nextInt(0,5), Random.nextInt(0,5))
            commandImpl.moveItem(i.toString(), randomValues)
        }
    }

    val queryModel = mutableMapOf<String, MovingItemDTO>()
    val projectionHandler = ProjectionHandler(eventStore, queryModel)

    projectionHandler.projectEvents()

    val queryHandler = QueryHandler(queryModel)

    queryHandler.getMovingItems().forEach{ dto ->
        println("Item: ${dto.name}, Location: ${dto.location}, Moves: ${dto.numberOfMoves}, Value: ${dto.value}")
    }

    val specificItem = queryHandler.getMovingItemByName("3")
    println("Specific item details: $specificItem")
}