import event.EventStore
import read.QueryImpl
import write.CommandHandler
import write.CommandImpl
import kotlin.random.Random

fun main(args: Array<String>) {
    val eventStore = EventStore()
    val commandHandler = CommandHandler(eventStore)
    val commandImpl = CommandImpl(commandHandler)

    val queryImpl = QueryImpl()

    commandImpl.createItem("5")
    val randomValues = Vector(Random.nextInt(0,5), Random.nextInt(0,5), Random.nextInt(0,5))
    commandImpl.moveItem("5", randomValues)


}