package transforms

import SensorData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.beam.sdk.io.kafka.KafkaRecord
import org.apache.beam.sdk.transforms.DoFn
import java.io.IOException


class JSONToSensorData : DoFn<KafkaRecord<String, String>, SensorData>(), java.io.Serializable {
    @ProcessElement
    fun processElement(
        @Element input: KafkaRecord<String, String>,
        output: OutputReceiver<SensorData>
    ) {
        val objectMapper = jacksonObjectMapper()
        return try {
            output.output(objectMapper.readValue(input.kv.value, SensorData::class.java))
        } catch (e: IOException) {
            println(e)
            output.output(SensorData("",0, listOf()))
        }
    }
}