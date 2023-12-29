package activemq

import org.apache.activemq.broker.BrokerService

fun main() {
    val broker = BrokerService()
    broker.isPersistent = false
    broker.brokerName = "myBroker"
    broker.addConnector("tcp://localhost:61616")
    broker.addConnector("ssl://localhost:61617")
    broker.addConnector("mqtt://localhost:1883")
    try {
        broker.start()
        broker.waitUntilStopped()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
}