package kafka

import initializeQuerySide
import projection.ProjectionHandler

fun main() {
    try {
        val (_, queryModel) = initializeQuerySide()
        val projectionHandler = ProjectionHandler(queryModel)
        startConsumer(projectionHandler)
    } finally {

    }
}

fun startConsumer(projectionHandler: ProjectionHandler) {
    val consumer = KafkaEventConsumer(projectionHandler)
    consumer.start()
}