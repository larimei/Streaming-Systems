package event

import MovingItemImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import projection.EventHandler
import read.MovingItemDTO
import write.MovingItem
import java.time.Duration

class EventStoreImpl(private val producer: Producer<String, String>) : EventStore {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun saveEvent(event: MovingItemEvent) {
        val jsonMessage = objectMapper.writeValueAsString(event)

        val topic = "StreamingTopic"
        val key = "Streaming"

        val message: ProducerRecord<String, String> = ProducerRecord(topic, key, jsonMessage)

        producer.send(message)
    }

    //create domainmodel
    override fun loadNamesOfMovingItems(): Map<String, MovingItem> {
        val eventHandler = EventHandler(mutableMapOf<String, MovingItemImpl>())
        val records = createConsumer()
        for (r in records) {
            System.out.printf(
                "offset = %d, key = %s, value = %s%n",
                r.offset(), r.key(), r.value()
            )

            try {
                val event = objectMapper.readValue(r.value(), MovingItemEvent::class.java)
                eventHandler.handleEvent(event)
            } catch (e: com.fasterxml.jackson.core.JsonParseException) {
                println("Invalid JSON: ${r.value()}")
            } catch (e: Exception) {
                println("Error processing record: ${r.value()}")
                e.printStackTrace()
            }
        }
        return mapOf()
    }

    private fun createConsumer(): ConsumerRecords<String?, String?> {
        val consumerProps = mapOf(
            "bootstrap.servers" to "localhost:9092, localhost:9093, localhost:9094",
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "group.id" to "someGroup",
            "security.protocol" to "PLAINTEXT"
        )

        val consumer: Consumer<String, String> = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf("StreamingTopic"))

        val duration = Duration.ofMillis(10000)
        return consumer.poll(duration)

    }
}