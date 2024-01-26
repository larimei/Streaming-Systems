package transforms

import SensorData
import org.apache.beam.sdk.transforms.Combine
import java.io.Serializable


class MeanCombineFn : Combine.CombineFn<Iterable<SensorData>, MeanCombineFn.Accum, Double>() {
    class Accum : Serializable {
        var sum = 0.0
        var count = 0
    }

    override fun createAccumulator(): Accum {
        return Accum()
    }

    override fun addInput(accum: Accum, input: Iterable<SensorData>): Accum {
        input.forEach { sensorData ->
            accum.sum += sensorData.speeds.sum()
            accum.count += sensorData.speeds.size
        }
        return accum
    }

    override fun mergeAccumulators(accumulators: Iterable<Accum>): Accum {
        val merged = createAccumulator()
        for (accum in accumulators) {
            merged.sum += accum.sum
            merged.count += accum.count
        }
        return merged
    }

    override fun extractOutput(accum: Accum): Double {
        return if (accum.count > 0) accum.sum / accum.count else 0.0
    }
}