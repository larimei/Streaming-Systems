package transforms

import SensorData
import consumer.ConsumerConfig
import org.apache.beam.sdk.transforms.DoFn
import kotlin.math.round


class ConvertToKmh : DoFn<SensorData, SensorData>() {
    @ProcessElement
    fun processElement(
        @Element input: SensorData, output: OutputReceiver<SensorData>
    ) {
        output.output(
            SensorData(
                input.sensorId,
                input.speeds.map { round(it * ConsumerConfig.KM_FACTOR * 10) / 10.0 })
        )
    }
}