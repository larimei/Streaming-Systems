package config

import com.espertech.esper.common.client.configuration.Configuration
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPRuntimeProvider
import models.*

fun setupEsperRuntime(): EPRuntime {
    val configuration = Configuration().apply {
        common.addEventType("SensorEvent", SensorEvent::class.java)
        common.addEventType("CleanSensorEvent", CleanSensorEvent::class.java)
        common.addEventType("ValueConversionEvent", ValueConversionEvent::class.java)
        common.addEventType("AvgSpeedSensorEvent", AvgSpeedSensorEvent::class.java)
        common.addEventType("TrafficJamWarningEvent", TrafficJamWarningEvent::class.java)
    }

    val runtime = EPRuntimeProvider.getDefaultRuntime(configuration)
    runtime.initialize()
    return runtime
}
