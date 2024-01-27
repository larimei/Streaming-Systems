import com.espertech.esper.runtime.client.EPRuntime
import config.setupEsperRuntime
import epl.avgSpeedEPL
import epl.cleanEPL
import epl.trafficJamWarningEPL
import epl.valueConversionEPL
import generator.DataGenerator
import generator.GeneratorConfig
import listener.AvgSpeedEventListener
import listener.CleanSensorEventListener
import listener.TrafficJamWarningEventListener
import listener.ValueConversionListener
import utils.compileAndDeploy
import utils.setupListener
import kotlin.random.Random

fun main() {
    val runtime = setupEsperRuntime()

    val cleanDeployment = compileAndDeploy(runtime, cleanEPL())
    setupListener(runtime, cleanDeployment, "CleanEvents", CleanSensorEventListener())

    val valueConversionDeployment = compileAndDeploy(runtime, valueConversionEPL())
    setupListener(runtime, valueConversionDeployment, "ValueConversionCalculation", ValueConversionListener())

    val avgSpeedDeployment = compileAndDeploy(runtime, avgSpeedEPL())
    setupListener(runtime, avgSpeedDeployment, "AvgSpeedCalculation", AvgSpeedEventListener())

    val trafficJamWarningDeployment = compileAndDeploy(runtime, trafficJamWarningEPL())
    setupListener(runtime, trafficJamWarningDeployment, "TrafficJamWarningCalculation", TrafficJamWarningEventListener())

    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    generateAndSendEvents(runtime, generator)
}

fun generateAndSendEvents(runtime: EPRuntime, generator: DataGenerator) {
    while (true) {
        val data = generator.generateData()
        println(data)
        runtime.eventService.sendEventBean(data, "SensorEvent")
        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))
    }
}