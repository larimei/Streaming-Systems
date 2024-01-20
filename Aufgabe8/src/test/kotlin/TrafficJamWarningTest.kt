import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class TrafficJamWarningTest {

    @Test
    fun testTrafficJamWarningEventListenerTriggered() {
        val runtime = setupEsperRuntime()

        val cleanDeployment = compileAndDeploy(runtime, cleanEPL())
        setupListener(runtime, cleanDeployment, "CleanEvents", CleanSensorEventListener())

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

        runtime.eventService.sendEventBean(SensorEvent(1, listOf(20.0, 21.0, 22.0)), "SensorEvent")
        runtime.eventService.sendEventBean(SensorEvent(2, listOf(20.0, 21.0, 22.0)), "SensorEvent")
        Thread.sleep(11000)

        runtime.eventService.sendEventBean(SensorEvent(1, listOf(10.0, 11.0, 12.0)), "SensorEvent")
        runtime.eventService.sendEventBean(SensorEvent(2, listOf(20.0, 21.0, 22.0)), "SensorEvent")
        Thread.sleep(11000)

        verify(trafficJamWarningListenerMock, times(1)).update(any(), any(), any(), any())
    }
}
