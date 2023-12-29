package activemq
import AppConfig
import javax.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory

object ActiveMQConnectionFactory {
    val instance: ConnectionFactory by lazy {
        ActiveMQConnectionFactory(AppConfig.BROKER_URL)
    }
}