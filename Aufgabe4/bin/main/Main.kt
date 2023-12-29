import AppConfig.ITEM_COUNT
import AppConfig.MAX_MOVES
import event.EventStoreImpl
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import read.MovingItemDTO
import read.QueryHandler
import write.CommandHandler
import write.CommandImpl
import kotlin.random.Random

fun main() {
    try {
        val producerProps = mapOf(
            "bootstrap.servers" to "localhost:9092, localhost:9093, localhost:9094",
            "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "security.protocol" to "PLAINTEXT"
        )

        val producer = KafkaProducer<String, String>(producerProps)

        val commandImpl = initializeCommandSide(producer)

        val (queryHandler, _) = initializeQuerySide()

        processItems(commandImpl, ITEM_COUNT, MAX_MOVES)

        printQueryResults(queryHandler)
    } finally {

    }
}

fun initializeCommandSide(connectionProducer: Producer<String, String>): CommandImpl {
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

    try {
        println("Specific item details: ${queryHandler.getMovingItemByName("3")}")
    } catch (e: NoSuchElementException) {
        println("Error: ${e.message}")
    }
}