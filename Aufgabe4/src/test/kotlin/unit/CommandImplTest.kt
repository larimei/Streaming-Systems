package unit

import MovingItemImpl
import Vector
import event.EventStore
import event.ItemMovedEvent
import event.MovingItemEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import write.CommandHandler
import write.CommandImpl
import java.util.concurrent.LinkedBlockingQueue

class CommandImplTest {

    private lateinit var eventStore: EventStore
    private lateinit var commandHandler: CommandHandler
    private lateinit var commandImpl: CommandImpl
    private val domainItems = mutableMapOf<String, MovingItemImpl>()
    private val eventQueue = LinkedBlockingQueue<MovingItemEvent>()

    @BeforeEach
    fun setUp() {
        eventStore = EventStore(eventQueue)
        commandHandler = CommandHandler(eventStore, domainItems)
        commandImpl = CommandImpl(commandHandler)
    }

    @Test
    fun `moveItem should save MovingItemMovedEvent to event store`() {

        val itemId = "5"
        val moveVector = Vector(1, 1, 1)

        commandImpl.createItem(itemId)
        commandImpl.moveItem(itemId, moveVector)

        val event = eventStore.getAllEvents().last()
        assertTrue(event is ItemMovedEvent)
        assertEquals(itemId, (event as ItemMovedEvent).id)
        assertEquals(moveVector, event.vector)
    }
}
