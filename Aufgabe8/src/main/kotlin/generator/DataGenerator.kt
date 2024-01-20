package generator

import models.SensorEvent
import kotlin.math.round
import kotlin.random.Random

class DataGenerator(
    private val sensorCount: Int,
    private val maxValueCount: Int,
    private val minSpeed: Double,
    private val maxSpeed: Double,
) {
    fun generateData(): SensorEvent {
        val sensorId = Random.nextInt(1, sensorCount + 1)
        val valueCount = if (Random.nextInt(1, 101) <= GeneratorConfig.NO_VALUES_PROBABILITY) 0
        else Random.nextInt(1, maxValueCount + 1)
        val speeds = (1..valueCount).map { generateSpeed() }

        return SensorEvent(sensorId, speeds)
    }

    private fun generateSpeed(): Double {
        val isNegative = Random.nextInt(1, 101) <= GeneratorConfig.NEGATIVE_VALUE_PROBABILITY
        val speed = roundSpeed(Random.nextDouble(minSpeed, maxSpeed))
        return if (isNegative) -speed else speed
    }

    private fun roundSpeed(speed: Double) = round(speed * 10) / 10.0
}