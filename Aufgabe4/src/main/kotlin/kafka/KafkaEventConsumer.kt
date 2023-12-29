package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import event.MovingItemEvent
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import projection.ProjectionHandler
import timeDifference
import java.time.Duration


class KafkaEventConsumer(private val projectionHandler: ProjectionHandler) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun start() {
        val consumerProps = mapOf(
            "bootstrap.servers" to "localhost:9092, localhost:9093, localhost:9094",
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "group.id" to "someGroup",
            "security.protocol" to "PLAINTEXT"
        )

        val consumer: Consumer<String, String> = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf("quickstart"))
        listen(consumer)
    }

    private fun listen(consumer: Consumer<String, String>) {
        val duration = Duration.ofMillis(10000)

        while (true) {
            val records: ConsumerRecords<String?, String?> = consumer.poll(duration)
            for (r in records) {
                System.out.printf(
                    "offset = %d, key = %s, value = %s%n",
                    r.offset(), r.key(), r.value()
                )

                val event = objectMapper.readValue(r.value(), MovingItemEvent::class.java)
                val timeDifferenceEvent = System.currentTimeMillis() - event.timestamp
                println("Event $event needed $timeDifferenceEvent to get to the consumer")
                timeDifference.add(timeDifferenceEvent)
                projectionHandler.projectEvent(event)
            }
        }
    }
}