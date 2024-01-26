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
import transforms.ConvertToKmh
import transforms.JSONToSensorData
import transforms.MeanCombineFn
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

        val validSensorData = sensorDataRecords.apply("Filter Negative and Zero Speeds", ParDo.of(object : DoFn<SensorData, SensorData>() {
            @ProcessElement
            fun processElement(@Element sensorData: SensorData, output: OutputReceiver<SensorData>) {
                val filteredSpeeds = sensorData.speeds.filter { it > 0 }
                output.output(SensorData(sensorData.timestamp, sensorData.sensorId, filteredSpeeds))
            }
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

        val averageSpeeds = groupedSensorData
            .apply("Calculate Average Speeds",  Combine.perKey(MeanCombineFn()))




        averageSpeeds.apply("Print Average Speeds", ParDo.of(object : DoFn<KV<Int, Double>, Void>() {
            @ProcessElement
            fun processElement(@Element input: KV<Int, Double>) {
                println("Sensor ID: ${input.key}, Average Speed: ${round(input.value * 10) / 10.0} kmh")
            }
        }))

        pipeline.run()
    }
}

