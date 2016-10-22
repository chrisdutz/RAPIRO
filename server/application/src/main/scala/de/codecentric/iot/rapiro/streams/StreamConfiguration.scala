package de.codecentric.iot.rapiro.streams

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import de.codecentric.iot.rapiro.akka.SpringExtension
import de.codecentric.iot.rapiro.akka.events.AddListenerEvent
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class StreamConfiguration extends InitializingBean {

  @Autowired private val actorSystem: ActorSystem = null

  @Autowired private implicit val materializer: Materializer = null


  override def afterPropertiesSet(): Unit = {
    val debugActor = actorSystem.actorOf(Props[DebugActor], "debugActor")
    val sceneDebugActor = actorSystem.actorOf(Props[SceneDebugActor], "sceneDebugActor")

    // Create all the actors used in this application.
    val movementActor: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("movementActor")
    )
    val telemetryActor: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryActor")
    )
    val visionActor:ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionActor")
    )

    movementActor.tell(new AddListenerEvent(debugActor), null)
    telemetryActor.tell(new AddListenerEvent(debugActor), null)
    visionActor.tell(new AddListenerEvent(sceneDebugActor), null)
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
    //val movementFlow = movementActorSource to movementPublisherSink
    //val telemetryFlow = telemetryActorSource to telemetryPublisherSink
    //val visionFlow = visionActorSource to visionPublisherSink


    // Start the flows
    //movementFlow.run()
    //telemetryFlow.run()
    //visionFlow.run()
  }

}
