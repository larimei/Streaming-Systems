package listener

import com.espertech.esper.common.client.EventBean
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPStatement
import com.espertech.esper.runtime.client.UpdateListener

class AvgSpeedEventListener : UpdateListener {
    override fun update(newEvents: Array<out EventBean>?, oldEvents: Array<out EventBean>?, statement: EPStatement?, runtime: EPRuntime?) {
        newEvents?.forEach { event ->
            println("Durchschnittsgeschwindigkeit: Sensor ${event.get("sensorId")} - ${event.get("avgSpeed")} km/h")
        }
    }
}