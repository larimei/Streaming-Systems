import com.espertech.esper.common.client.EventBean
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPStatement
import com.espertech.esper.runtime.client.UpdateListener

class TrafficJamWarningEventListener : UpdateListener {
    override fun update(newEvents: Array<out EventBean>?, oldEvents: Array<out EventBean>?, statement: EPStatement?, runtime: EPRuntime?) {
        newEvents?.forEach { event ->
            println("Stauwarnung: Sensor ${event.get("sensorId")} - ${event.get("avgSpeedDrop")}")
        }
    }
}