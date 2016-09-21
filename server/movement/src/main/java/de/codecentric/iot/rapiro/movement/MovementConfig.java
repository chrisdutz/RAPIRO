package de.codecentric.iot.rapiro.movement;

import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import de.codecentric.iot.rapiro.akka.SpringExtension;
import de.codecentric.iot.rapiro.kafka.KafkaConfig;
import de.codecentric.iot.rapiro.movement.model.Position;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by christoferdutz on 13.09.16.
 */
@Configuration
public class MovementConfig implements InitializingBean {

    private static final String SERVICE_DESTINATION = "movementEvents";

    @Autowired
    private MessageBroker broker;

    @Autowired
    private ActorSystem actorSystem;

    /**
     * Initialize all of the parts related to collecting movement
     * data and routing that to the client.
     */
    @Override
    public void afterPropertiesSet() {
        initAkkaFlow();
        initBlazeDsDestinations();
        initKafkaStreams();
    }

    /**
     * Initialize the akka actors for collecting movement data.
     *
     * What happens here, is that we create an Akka Flow which routes
     * all PositionProtocol.Position the "movementManager" actor produces
     * and routes these to an Akka sink implementation that simply forwards
     * every message to the corresponding Kafka topic "movement".
     *
     * As soon as the actor is created it starts auto-emitting messages
     * as it requests to be sent an activation message every 100ms by the
     * Akka scheduler.
     */
    private void initAkkaFlow() {
        // The Materializer will take a flow description and create the resources it needs
        // to operate. As we are building an actor based system, the ActorMaterializer
        // handles creation of the actors themselves.
        final Materializer materializer = ActorMaterializer.create(actorSystem);

        // Create a sink for the Akka ProducerRecors and make that sink send the
        // Payload of the messages to a Kafka topic, which is provided by the
        // ProducerRecord instance.
        ProducerSettings<byte[], String> producerSettings = ProducerSettings.apply(actorSystem,
                new ByteArraySerializer(), new StringSerializer())
                .withBootstrapServers("localhost:" + KafkaConfig.KAFKA_SERVER_PORT);
        Sink kafkaProducer = Producer.plainSink(producerSettings);

        // Define the akka flow to take PositionProtocol.Position items
        // produced by a Spring managed "movementManager" actor and route them
        // to a Kafka producer (So don't be confused why the items are
        // actually created by an actor and consumed by a producer, this is
        // just from the Kafka point of view the component that produces the
        // Kafka flow).
        Flow.of(Position.class).map(elem ->
                new ProducerRecord<>("movement", elem)
        ).to(kafkaProducer).runWith(Source.actorPublisher(
                SpringExtension.SpringExtProvider.get(actorSystem).props("movementManager")),
                materializer);
    }

    /**
     * Initializes the BlazeDS messaging destinations clients can subscribe to.
     */
    private void initBlazeDsDestinations() {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination visionEvents = service.createDestination(SERVICE_DESTINATION);
        service.addDestination(visionEvents);
        service.start();
    }

    /**
     * Initialize the Kafka streams routing the information gathered by the Akka actors
     * to the BlazeDS messaging destinations.
     */
    private void initKafkaStreams() {

    }

}
