import org.apache.beam.sdk.coders.SerializableCoder
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.testing.PAssert
import org.apache.beam.sdk.testing.TestPipeline
import org.apache.beam.sdk.transforms.Create
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection
import kotlin.test.Test

class KafkaConsumerTest {
    @Test
    fun testFilterNegativeAndZeroSpeeds() {
        val options = PipelineOptionsFactory.create()
        val pipeline = TestPipeline.create(options)

        val testInput = listOf(
            SensorData("",1, listOf(10.0, -1.0, 0.0, 30.0)),
            SensorData("",2, listOf(20.0, 30.0)),
            SensorData("",3, listOf(-5.0, 0.0, 15.0))
        )

        val inputPCollection: PCollection<SensorData> = pipeline
            .apply("Erstelle Testdaten", Create.of(testInput))
            .setCoder(SerializableCoder.of(SensorData::class.java))

        val filteredSensorData = inputPCollection.apply("Filter Negative and Zero Speeds", ParDo.of(object : DoFn<SensorData, SensorData>() {
            @ProcessElement
            fun processElement(@Element sensorData: SensorData, output: OutputReceiver<SensorData>) {
                val filteredSpeeds = sensorData.speeds.filter { it > 0 }
                output.output(SensorData("",sensorData.sensorId, filteredSpeeds))
            }
        }))

        PAssert.that(filteredSensorData).containsInAnyOrder(
            SensorData("",1, listOf(10.0, 30.0)),
            SensorData("",2, listOf(20.0, 30.0)),
            SensorData("",3, listOf(15.0))
        )

        pipeline.run().waitUntilFinish()
    }
}
