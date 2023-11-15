package activemq

import AppConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import event.MovingItemEvent
import projection.ProjectionHandler
import javax.jms.*

class ActiveMQEventConsumer(private val connection: Connection, private val projectionHandler: ProjectionHandler) : MessageListener {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    fun start() {
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        try {
            val destination = session.createQueue(AppConfig.QUEUE_NAME)
            val consumer = session.createConsumer(destination)
            consumer.messageListener = this
        } catch (e: Exception) {
            session.close()
        }
    }

    override fun onMessage(message: Message) {
        try {
            if (message is TextMessage) {
                val json = message.text
                val event = objectMapper.readValue(json, MovingItemEvent::class.java)
                projectionHandler.projectEvent(event)
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
        }
    }
}
