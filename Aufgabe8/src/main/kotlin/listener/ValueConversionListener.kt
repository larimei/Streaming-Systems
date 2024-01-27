package listener

import AppConfig
import com.espertech.esper.common.client.EventBean
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPStatement
import com.espertech.esper.runtime.client.UpdateListener
import models.ValueConversionEvent
import kotlin.math.round

class ValueConversionListener : UpdateListener {
    override fun update(
        newEvents: Array<out EventBean>?,
        oldEvents: Array<out EventBean>?,
        statement: EPStatement?,
        runtime: EPRuntime?
    ) {
        newEvents?.forEach { event ->
            val sensorId = event.get("sensorId") as Int
            val speed = event.get("speed") as Double
            val convertedSpeed = round(speed * AppConfig.KM_FACTOR * 10) / 10.0
            runtime?.eventService?.sendEventBean(ValueConversionEvent(sensorId, convertedSpeed), "ValueConversionEvent")
        }
    }
}