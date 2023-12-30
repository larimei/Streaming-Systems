import kotlin.random.Random

fun main() {
    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    while (true) {
        println(generator.generateData())
        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))
    }
}