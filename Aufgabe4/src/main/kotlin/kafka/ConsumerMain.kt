package kafka

import projection.ProjectionHandler
import read.MovingItemDTO
import read.QueryHandler

fun main() {
    try {
        val (queryHandler, queryModel) = initializeQuerySide()
        val projectionHandler = ProjectionHandler(queryModel)
        startConsumer(projectionHandler, queryHandler)
    } finally {

    }
}

fun startConsumer(projectionHandler: ProjectionHandler, queryHandler: QueryHandler) {
    val consumer = KafkaEventConsumer(projectionHandler, queryHandler)
    consumer.start()
}

fun initializeQuerySide(): Pair<QueryHandler, MutableMap<String, MovingItemDTO>> {
    val queryModel = mutableMapOf<String, MovingItemDTO>()
    val queryHandler = QueryHandler(queryModel)
    return Pair(queryHandler, queryModel)
}