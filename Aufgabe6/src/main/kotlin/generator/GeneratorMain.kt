package generator

import AppConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import kotlin.random.Random

fun main() {
    val producerProps = mapOf(
        "bootstrap.servers" to "localhost:9092",
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "security.protocol" to "PLAINTEXT"
    )

    var keyCounter = 0

    val producer = KafkaProducer<String, String>(producerProps)

    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    while (true) {
        val data = generator.generateData()
        val key = keyCounter++.toString()
        val message: ProducerRecord<String, String> = ProducerRecord(AppConfig.TOPIC, key, data)
        producer.send(message)
        println(data)
        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))
    }
}