package unit

import Vector
import event.EventStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import write.CommandHandler
import write.CommandImpl

class CommandImplTest {

    private lateinit var eventStore: EventStore
    private lateinit var commandHandler: CommandHandler
    private lateinit var commandImpl: CommandImpl

    @BeforeEach
    fun setUp() {
        // Set up your mocks and test data here
        eventStore = mock(EventStore::class.java)
        commandHandler = CommandHandler(eventStore)
        commandImpl = CommandImpl(commandHandler)
    }

    @Test
    fun `moveItem should save MovingItemMovedEvent to event store`() {
        // Arrange
        val itemId = "5"
        val moveVector = Vector(1, 1, 1)
        doNothing().`when`(eventStore).saveEvent(any())

        // Act
        commandImpl.createItem(itemId) // Assuming this method is needed to initialize the item
        commandImpl.moveItem(itemId, moveVector)

        // Assert
        verify(eventStore, times(1)).saveEvent(any(MovingItemMovedEvent::class.java))
    }
}
