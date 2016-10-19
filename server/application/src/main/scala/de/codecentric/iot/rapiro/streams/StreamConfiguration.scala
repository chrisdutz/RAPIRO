package de.codecentric.iot.rapiro.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import de.codecentric.iot.rapiro.akka.SpringExtension
import de.codecentric.iot.rapiro.movement.model.Position
import de.codecentric.iot.rapiro.vision.model.Scene
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class StreamConfiguration extends InitializingBean {

  @Autowired private val actorSystem: ActorSystem = null

  @Autowired private implicit val materializer: Materializer = null


  override def afterPropertiesSet(): Unit = {
    // Create all the actors used in this application.
    val movementActorSource: Source[Position, ActorRef] = Source.actorPublisher[Position](
      SpringExtension.SpringExtProvider.get(actorSystem).props("positionActor")
    )
    val telemetryActorSource: Source[Position, ActorRef] = Source.actorPublisher[Position](
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryActor")
    )
    val visionActorSource: Source[Scene, ActorRef] = Source.actorPublisher[Scene](
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionActor")
    )


    // Create any actors that process the stream.



    // Create any actors that will publish events to BlazeDS topics.
    val movementPublisherSink = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("movementPublishingActor")
    )
    val telemetryPublisherSink = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryPublishingActor")
    )
    val visionPublisherSink = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionPublishingActor")
    )


    // Define the flows
    val movementFlow = movementActorSource to movementPublisherSink
    val telemetryFlow = telemetryActorSource to telemetryPublisherSink
    val visionFlow = visionActorSource to visionPublisherSink


    // Start the flows
    movementFlow.run()
    telemetryFlow.run()
    visionFlow.run()
  }

}
