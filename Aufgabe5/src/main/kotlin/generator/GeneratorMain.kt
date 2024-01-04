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

    //val objectMapper: ObjectMapper = jacksonObjectMapper()
    val producer = KafkaProducer<String, String>(producerProps)

    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    while (true) {
        val data = generator.generateData()
        val message: ProducerRecord<String, String> = ProducerRecord(AppConfig.TOPIC,AppConfig.TOPIC_KEY, data)
        producer.send(message)
        println(data)
        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))
    }
}