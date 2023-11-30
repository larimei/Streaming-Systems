package event

import AppConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.jms.Connection
import javax.jms.Session

class EventStoreImpl(private val connection: Connection): EventStore {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun saveEvent(event: MovingItemEvent) {
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        val destination = session.createQueue(AppConfig.QUEUE_NAME)

        val producer = session.createProducer(destination)
        val jsonMessage = objectMapper.writeValueAsString(event)
        val message = session.createTextMessage(jsonMessage)

        producer.send(message)

        session.close()
    }
}