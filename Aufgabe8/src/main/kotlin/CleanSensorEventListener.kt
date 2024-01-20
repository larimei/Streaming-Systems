
import com.espertech.esper.common.client.EventBean
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPStatement
import com.espertech.esper.runtime.client.UpdateListener

class CleanSensorEventListener : UpdateListener {
    override fun update(newEvents: Array<out EventBean>?, oldEvents: Array<out EventBean>?, statement: EPStatement?, runtime: EPRuntime?) {
        newEvents?.forEach { event ->
            val sensorId = event.get("sensorId") as Int
            val speeds: List<Double> = when (val rawSpeeds = event.get("speeds")) {
                is List<*> -> rawSpeeds.filterIsInstance<Double>()
                else -> emptyList()
            }

            val filterNegativeSpeeds = speeds.filter { it >= 0.0 }

            if (filterNegativeSpeeds.isNotEmpty()) {
                filterNegativeSpeeds.forEach{
                    runtime?.eventService?.sendEventBean(CleanSensorEvent(sensorId, it), "CleanSensorEvent")
                }
            }
        }
    }
}