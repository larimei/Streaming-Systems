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

    commandImpl.createItem("7")
    for (i in 1..21) {
        val randomValues = Vector(Random.nextInt(0,5), Random.nextInt(0,5), Random.nextInt(0,5))
        commandImpl.moveItem("7", randomValues)
    }

    val vector = Vector(8, 8, 8)

    commandImpl.createItem("8")
    commandImpl.moveItem("8", vector)

    commandImpl.createItem("9")
    commandImpl.moveItem("9", vector)

    commandImpl.createItem("10")
    commandImpl.moveItem("10", vector)

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