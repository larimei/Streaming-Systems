import java.io.Serializable

data class SensorData(
    val timestamp: String,
    val sensorId: Int,
    val speeds: List<Double>
): Serializable