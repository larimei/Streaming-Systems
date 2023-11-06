package integration

import MovingItemImpl
import Vector
import event.EventStore
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
        // Arrange
        val eventQueue = LinkedBlockingQueue<MovingItemEvent>()
        val eventStore = EventStore(eventQueue)
        val domainItems = mutableMapOf<String, MovingItemImpl>()
        val commandHandler = CommandHandler(eventStore, domainItems)
        val commandImpl = CommandImpl(commandHandler)
        val queryModel = mutableMapOf<String, MovingItemDTO>()
        val projectionHandler = ProjectionHandler(eventStore, queryModel)
        val itemId = "item1"
        commandImpl.createItem(itemId)

        // Act
        val moveVector = Vector(1, 1, 1)
        commandImpl.moveItem(itemId, moveVector)
        projectionHandler.projectEvents()

        // Assert
        val dto = queryModel[itemId]
        assertNotNull(dto)
        assertEquals(moveVector.toString(), dto?.location)
        assertEquals(1, dto?.numberOfMoves)
    }
}
