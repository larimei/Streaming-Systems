import AppConfig.ITEM_COUNT
import AppConfig.MAX_MOVES
import activemq.ActiveMQConnectionFactory
import activemq.ActiveMQEventConsumer
import event.EventStoreImpl
import projection.ProjectionHandler
import read.MovingItemDTO
import read.QueryHandler
import write.CommandHandler
import write.CommandImpl
import javax.jms.Connection
import kotlin.random.Random

fun main() {
    val connectionProducer = ActiveMQConnectionFactory.instance.createConnection().apply { start() }
    val connectionConsumer = ActiveMQConnectionFactory.instance.createConnection().apply { start() }
    try {
        val commandImpl = initializeCommandSide(connectionProducer)
        val (queryHandler, queryModel) = initializeQuerySide()
        val projectionHandler = ProjectionHandler(queryModel)
        startConsumer(connectionConsumer, projectionHandler)

        processItems(commandImpl, ITEM_COUNT, MAX_MOVES)

        printQueryResults(queryHandler)
    } finally {
        connectionConsumer.close()
        connectionProducer.close()
    }
}

fun initializeCommandSide(connectionProducer: Connection): CommandImpl {
    val eventStore = EventStoreImpl(connectionProducer)
    val domainItems = mutableMapOf<String, MovingItemImpl>()
    val commandHandler = CommandHandler(eventStore, domainItems)
    return CommandImpl(commandHandler)
}

fun initializeQuerySide(): Pair<QueryHandler, MutableMap<String, MovingItemDTO>> {
    val queryModel = mutableMapOf<String, MovingItemDTO>()
    val queryHandler = QueryHandler(queryModel)
    return Pair(queryHandler, queryModel)
}

fun startConsumer(connection: Connection, projectionHandler: ProjectionHandler) {
    val consumer = ActiveMQEventConsumer(connection, projectionHandler)
    consumer.start()
}

fun processItems(commandImpl: CommandImpl, itemCount: Int, maxMoves: Int) {
    (1..itemCount).forEach { itemId ->
        createAndMoveItem(commandImpl, itemId.toString(), null, maxMoves)
    }
    createAndMoveItem(commandImpl, "7", moveCount = 20)

    val vector = Vector(8, 8, 8)
    listOf("8", "9", "10").forEach { itemId ->
        commandImpl.createItem(itemId)
        commandImpl.moveItem(itemId, vector)
    }
}

fun createAndMoveItem(commandImpl: CommandImpl, itemId: String, moveCount: Int? = null, maxMoves: Int = 5) {
    commandImpl.createItem(itemId)
    val count = moveCount ?: Random.nextInt(0, maxMoves)
    repeat(count) {
        val randomValues = Vector(Random.nextInt(0, 5), Random.nextInt(0, 5), Random.nextInt(0, 5))
        commandImpl.moveItem(itemId, randomValues)
    }
}

fun printQueryResults(queryHandler: QueryHandler) {
    queryHandler.getMovingItems().forEach { dto ->
        println("Item: ${dto.name}, Location: ${dto.location}, Moves: ${dto.numberOfMoves}, Value: ${dto.value}")
    }

    println("Specific item details: ${queryHandler.getMovingItemByName("3")}")
}