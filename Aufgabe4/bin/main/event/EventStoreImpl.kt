package event

import AppConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class EventStoreImpl(private val producer: Producer<String, String>): EventStore {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun saveEvent(event: MovingItemEvent) {
        val jsonMessage = objectMapper.writeValueAsString(event)

        val topic = "StreamingTopic"
        val key = "Streaming"

        val message: ProducerRecord<String, String> = ProducerRecord(topic, key, jsonMessage)

        producer.send(message)
    }
}