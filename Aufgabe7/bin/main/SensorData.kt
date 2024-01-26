import java.io.Serializable

data class SensorData(
    val sensorId: Int,
    val speeds: List<Double>
): Serializable