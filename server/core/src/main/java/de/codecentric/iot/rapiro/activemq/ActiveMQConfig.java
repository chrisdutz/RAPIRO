package de.codecentric.iot.rapiro.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;

/**
 * Created by christoferdutz on 23.09.16.
 *
 * Interesting Links:
 * http://activemq.apache.org/mqtt.html
 * http://activemq.apache.org/how-do-i-embed-a-broker-inside-a-connection.html
 *
 */
@Profile("activemq")
@Configuration
public class ActiveMQConfig {

    @Bean
    public BrokerService brokerService() {
        BrokerService broker = new BrokerService();

        try {
            // Initialize an mqtt connector.
            TransportConnector connector = new TransportConnector();
            connector.setName("mqtt+nio");
            connector.setUri(new URI("mqtt+nio://localhost:1883"));
            broker.addConnector(connector);

            broker.start();

            return broker;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
