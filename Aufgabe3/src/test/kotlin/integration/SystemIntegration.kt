package integration

import Vector
import activemq.ActiveMQConnectionFactory
import initializeCommandSide
import initializeQuerySide
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import projection.ProjectionHandler
import startConsumer

class SystemIntegrationTest {

    @Test
    fun `moveItem reflects in QueryModel after projection`() {

        val connectionProducer = ActiveMQConnectionFactory.instance.createConnection().apply { start() }
        val connectionConsumer = ActiveMQConnectionFactory.instance.createConnection().apply { start() }
        val commandImpl = initializeCommandSide(connectionProducer)
        val (queryHandler, queryModel) = initializeQuerySide()
        val projectionHandler = ProjectionHandler(queryModel)
        startConsumer(connectionConsumer, projectionHandler)

        val itemId = "item1"
        commandImpl.createItem(itemId)

        val moveVector = Vector(1, 1, 1)
        commandImpl.moveItem(itemId, moveVector)

        Thread.sleep(3000)
        assertEquals(moveVector, queryHandler.getMovingItemByName(itemId).location)
    }
}
