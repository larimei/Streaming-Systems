import consumer.ConsumerConfig
import consumer.KafkaConsumer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.round
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class KafkaConsumerTest {
    @Test
    fun `test private method processSensorData for speed conversion`() {
        val kafkaConsumer = KafkaConsumer()
        val processSensorDataMethod = KafkaConsumer::class.declaredMemberFunctions
            .firstOrNull { it.name == "processSensorData" }
            ?: throw NoSuchMethodException("Method processSensorData not found")

        processSensorDataMethod.isAccessible = true

        val originalSpeedsMs = listOf(20.4, 33.5, 40.0)

        val expectedSpeedsKmh = originalSpeedsMs.map { round(it * ConsumerConfig.KM_FACTOR* 10) /10.0 }

        val sensorData = SensorData("2024-01-01T10:01:29.551Z", 1, originalSpeedsMs)
        processSensorDataMethod.call(kafkaConsumer, sensorData, System.currentTimeMillis())

        val sensorSpeeds = kafkaConsumer::class.memberProperties
            .firstOrNull { it.name == "sensorSpeeds" }
            ?.also { it.isAccessible = true }
            ?.call(kafkaConsumer) as? Map<Int, List<Double>>

        assertNotNull(sensorSpeeds)
        assertTrue(sensorSpeeds?.containsKey(1) ?: false)
        assertEquals(expectedSpeedsKmh, sensorSpeeds?.get(1))
    }

    @Test
    fun `test processSensorData with negative speeds`() {
        val kafkaConsumer = KafkaConsumer()
        val processSensorDataMethod = KafkaConsumer::class.declaredMemberFunctions
            .firstOrNull { it.name == "processSensorData" }
            ?: throw NoSuchMethodException("Method processSensorData not found")

        processSensorDataMethod.isAccessible = true

        val sensorData = SensorData("2023-09-23T10:01:29.551Z", 1, listOf(-10.0, 20.0, 30.0))
        processSensorDataMethod.call(kafkaConsumer, sensorData, System.currentTimeMillis())

        val sensorSpeeds = kafkaConsumer::class.memberProperties
            .firstOrNull { it.name == "sensorSpeeds" }
            ?.also { it.isAccessible = true }
            ?.call(kafkaConsumer) as? Map<Int, List<Double>>

        assertTrue(sensorSpeeds?.get(1)?.all { it >= 0 } ?: false)
    }
}
