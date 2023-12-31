package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import event.MovingItemEvent
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import projection.ProjectionHandler
import read.QueryHandler
import java.time.Duration


class KafkaEventConsumer(private val projectionHandler: ProjectionHandler, private val queryHandler: QueryHandler) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun start() {
        val consumerProps = mapOf(
            "bootstrap.servers" to "localhost:9092",
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "group.id" to "someGroup",
            "security.protocol" to "PLAINTEXT"
        )

        val consumer: Consumer<String, String> = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf("StreamingTopic"))
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

                try {
                    val event = objectMapper.readValue(r.value(), MovingItemEvent::class.java)
                    projectionHandler.projectEvent(event)
                } catch (e: com.fasterxml.jackson.core.JsonParseException) {
                    println("Invalid JSON: ${r.value()}")
                } catch (e: Exception) {
                    println("Error processing record: ${r.value()}")
                    e.printStackTrace()
                }
            }

            printQueryResults(queryHandler)
        }
    }

    private fun printQueryResults(queryHandler: QueryHandler) {
        queryHandler.getMovingItems().forEach { dto ->
            println("Item: ${dto.name}, Location: ${dto.location}, Moves: ${dto.numberOfMoves}, Value: ${dto.value}")
        }

        try {
            println("Specific item details: ${queryHandler.getMovingItemByName("3")}")
        } catch (e: NoSuchElementException) {
            println("Error: ${e.message}")
        }
    }
}