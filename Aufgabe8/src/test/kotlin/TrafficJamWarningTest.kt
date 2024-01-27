import models.SensorEvent
import config.setupEsperRuntime
import epl.avgSpeedEPL
import epl.cleanEPL
import epl.trafficJamWarningEPL
import epl.valueConversionEPL
import listener.AvgSpeedEventListener
import listener.CleanSensorEventListener
import listener.TrafficJamWarningEventListener
import listener.ValueConversionListener
import models.ValueConversionEvent
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import utils.compileAndDeploy
import utils.setupListener

class TrafficJamWarningTest {

    @Test
    fun testTrafficJamWarningEventListenerTriggered() {
        val runtime = setupEsperRuntime()

        val cleanDeployment = compileAndDeploy(runtime, cleanEPL())
        setupListener(runtime, cleanDeployment, "CleanEvents", CleanSensorEventListener())

        val valueConversionDeployment = compileAndDeploy(runtime, valueConversionEPL())
        setupListener(runtime, valueConversionDeployment, "ValueConversionCalculation", ValueConversionListener())

        val avgSpeedDeployment = compileAndDeploy(runtime, avgSpeedEPL())
        setupListener(runtime, avgSpeedDeployment, "AvgSpeedCalculation", AvgSpeedEventListener())

        val trafficJamWarningListenerMock = Mockito.mock(TrafficJamWarningEventListener::class.java)
        val trafficJamWarningDeployment = compileAndDeploy(runtime, trafficJamWarningEPL())
        setupListener(
            runtime,
            trafficJamWarningDeployment,
            "TrafficJamWarningCalculation",
            trafficJamWarningListenerMock
        )

        runtime.eventService.sendEventBean(ValueConversionEvent(1, 70.0), "ValueConversionEvent")
        runtime.eventService.sendEventBean(ValueConversionEvent(2, 70.0), "ValueConversionEvent")
        Thread.sleep(11000)

        runtime.eventService.sendEventBean(ValueConversionEvent(1, 30.0), "ValueConversionEvent")
        runtime.eventService.sendEventBean(ValueConversionEvent(2, 60.0), "ValueConversionEvent")
        Thread.sleep(11000)

        verify(trafficJamWarningListenerMock, times(1)).update(any(), any(), any(), any())
    }
}
