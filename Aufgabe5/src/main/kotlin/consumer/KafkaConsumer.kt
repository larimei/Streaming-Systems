package consumer

import AppConfig
import SensorData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.round


class KafkaConsumer {
    private val objectMapper = jacksonObjectMapper()
    private val sensorSpeeds = mutableMapOf<Int, MutableList<Double>>()
    private var lastProcessedTime: Instant = Instant.now()
    private var counter = 1

    fun start() {
        val consumerProps = mapOf(
            "bootstrap.servers" to "localhost:9092",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "group.id" to "someGroup",
            "security.protocol" to "PLAINTEXT"
        )

        val consumer: Consumer<String, String> = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf(AppConfig.TOPIC))
        listen(consumer)
    }

    private fun listen(consumer: Consumer<String, String>) {
        val duration = Duration.ofMillis(10000)

        while (true) {
            val records: ConsumerRecords<String?, String?> = consumer.poll(duration)
            for (record in records) {
                try {
                    val sensorData = objectMapper.readValue(record.value(), SensorData::class.java)
                    processSensorData(sensorData, record.timestamp())
                } catch (e: com.fasterxml.jackson.core.JsonParseException) {
                    println("Invalid JSON: ${record.value()}")
                } catch (e: Exception) {
                    println("Error processing record: ${record.value()}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun processSensorData(sensorData: SensorData, kafkaTimestamp: Long) {
        val validSpeeds = sensorData.speeds.filter { it >= 0 }
        if (validSpeeds.isNotEmpty()) {
            val kmSpeeds = validSpeeds.map { round(it * ConsumerConfig.KM_FACTOR* 10) /10.0 }
            sensorSpeeds.getOrPut(sensorData.sensorId) { mutableListOf() }.addAll(kmSpeeds)
        }

        val recordTimestamp = Instant.ofEpochMilli(kafkaTimestamp)
        if (ChronoUnit.SECONDS.between(lastProcessedTime, recordTimestamp) >= ConsumerConfig.TIME_WINDOW) {
            calculateAndPrintAverages()
            lastProcessedTime = recordTimestamp
        }
    }

    private fun calculateAndPrintAverages() {
        println("---------------------------------------------")
        println("${ConsumerConfig.TIME_WINDOW}s passed ${counter}. data is available" )
        counter++
        sensorSpeeds.forEach { (sensorId, speeds) ->
            val averageSpeed = if (speeds.isNotEmpty()) {
                round(speeds.average() * 10) / 10.0
            } else {
                0.0
            }
            println("Sensor $sensorId: average speed = $averageSpeed km/h")
            println("With those speeds: $speeds")
        }
        sensorSpeeds.clear()
    }
}