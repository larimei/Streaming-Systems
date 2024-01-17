package consumer

fun main() {
    try {
        val consumer = KafkaConsumer()
        consumer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}