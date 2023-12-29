package kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord


fun main() {
    val producerProps = mapOf<String, String>(
        "bootstrap.servers" to "localhost:9092, localhost:9093, localhost:9094",
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "security.protocol" to "PLAINTEXT"
    )

    val producer = KafkaProducer<String, String>(producerProps)

    val topic = "quickstart"
    val key = "Manning"
    val value = "Event Processing in Action"
    val message: ProducerRecord<String, String> = ProducerRecord(topic, key, value)

    producer.send(message)
    readln()
    producer.close()
}

