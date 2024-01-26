package integration

import MovingItemImpl
import Vector
import event.EventStore
import event.EventStoreImpl
import event.MovingItemEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import projection.ProjectionHandler
import read.MovingItemDTO
import write.CommandHandler
import write.CommandImpl
import java.util.concurrent.LinkedBlockingQueue

class SystemIntegrationTest {

    @Test
    fun `moveItem reflects in QueryModel after projection`() {

        val eventQueue = LinkedBlockingQueue<MovingItemEvent>()
        val eventStore = EventStoreImpl(eventQueue)
        val domainItems = mutableMapOf<String, MovingItemImpl>()
        val commandHandler = CommandHandler(eventStore, domainItems)
        val commandImpl = CommandImpl(commandHandler)
        val queryModel = mutableMapOf<String, MovingItemDTO>()
        val projectionHandler = ProjectionHandler(eventStore, queryModel)
        val itemId = "item1"
        commandImpl.createItem(itemId)

        val moveVector = Vector(1, 1, 1)
        commandImpl.moveItem(itemId, moveVector)
        projectionHandler.projectEvents()

        val dto = queryModel[itemId]
        assertNotNull(dto)
        assertEquals(moveVector, dto?.location)
        assertEquals(1, dto?.numberOfMoves)
    }
}
