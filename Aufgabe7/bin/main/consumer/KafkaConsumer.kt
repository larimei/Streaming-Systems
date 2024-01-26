package consumer

import AppConfig
import SensorData
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.coders.SerializableCoder
import org.apache.beam.sdk.io.kafka.KafkaIO
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.*
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.apache.beam.sdk.transforms.windowing.Window
import org.apache.beam.sdk.values.KV
import org.apache.kafka.common.serialization.StringDeserializer
import org.joda.time.Duration
import transforms.JSONToSensorData
import java.util.*
import kotlin.math.round


class KafkaConsumer {
    fun start() {

        val options = PipelineOptionsFactory.create()
        val pipeline: Pipeline = Pipeline.create(options)

        val kafkaRead = KafkaIO.read<String, String>().withBootstrapServers("localhost:9092")
            .withKeyDeserializer(StringDeserializer::class.java).withValueDeserializer(StringDeserializer::class.java)
            .withTopic(AppConfig.TOPIC)

        val kafkaRecords = pipeline.apply("Read from Kafka", kafkaRead)


        val sensorDataRecords = kafkaRecords.apply(
            "JSON",
            ParDo.of(JSONToSensorData())
        ).setCoder(SerializableCoder.of(SensorData::class.java))

        val validSensorData =
            sensorDataRecords.apply(Filter.by(SerializableFunction { sensorData ->
                sensorData != null && sensorData.speeds.all { it >= 0 }
            }))

        val sensorDataInKmh = validSensorData.apply(
            "Convert to km/h", ParDo.of(ConvertToKmh())
        )


        val windowedSensorData = sensorDataInKmh.apply(
            "Window intervals",
            Window.into(FixedWindows.of(Duration.standardSeconds(ConsumerConfig.TIME_WINDOW.toLong())))
        )

        val groupedSensorData = windowedSensorData
            .apply(
                "Extract Sensor ID",
                ParDo.of(object : DoFn<SensorData, KV<Int, SensorData>>() {
                    @ProcessElement
                    fun processElement(
                        @Element input: SensorData,
                        receiver: OutputReceiver<KV<Int, SensorData>>
                    ) {
                        receiver.output(KV.of(input.sensorId, input))
                    }
                })
            )
            .apply(
                "Group by Sensor ID",
                GroupByKey.create()
            )

        val averageSpeeds = groupedSensorData.apply(
            "Calculate Average Speeds",
            Combine.perKey(SerializableFunction<KV<Int, Iterable<SensorData>>, Double> { input ->
                val speedsList = input.value.flatMap { it -> it.speeds }
                speedsList.sum() / speedsList.size
            }).withInputCoder(SerializableCoder.of(Double::class.java))
                .withOutputCoder(SerializableCoder.of(Double::class.java))
        )




        averageSpeeds.apply("Print Average Speeds", ParDo.of(object : DoFn<KV<Int, Double>, Void>() {
            @ProcessElement
            fun processElement(@Element input: KV<Int, Double>) {
                println("Sensor ID: ${input.key}, Average Speed: ${input.value}")
            }
        }))

        pipeline.run()
    }
}


internal class ConvertToKmh : DoFn<SensorData, SensorData>() {
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