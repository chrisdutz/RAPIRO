package de.codecentric.iot.rapiro.streams

import akka.actor.{ActorRef, ActorSystem}
import de.codecentric.iot.rapiro.akka.SpringExtension
import de.codecentric.iot.rapiro.akka.events.AddListenerEvent
import flex.messaging.MessageBroker
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.flex.messaging.MessageTemplate

@Configuration
class StreamConfiguration extends InitializingBean {

  @Autowired private val actorSystem: ActorSystem = null

  // Just to make sure the blazeds stuff is initialized first.
  @Autowired private val messageBroker: MessageBroker = null
  @Autowired private val messageTemplate: MessageTemplate = null

  override def afterPropertiesSet(): Unit = {
    //val debugActor = actorSystem.actorOf(Props[DebugActor], "debugActor")
    //val sceneDebugActor = actorSystem.actorOf(Props[SceneDebugActor], "sceneDebugActor")

    // Create all the actors used in this application.
    val movementActor: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("movementActor")
    )
    val telemetryActor: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryActor")
    )
    val visionActor: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionActor")
    )

    // Create any actors that will publish events to BlazeDS topics.
    val movementPublisherSink: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("movementPublishingActor")
    )
    val telemetryPublisherSink: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryPublishingActor")
    )
    val visionPublisherSink: ActorRef = actorSystem.actorOf(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionPublishingActor")
    )

    // Connect the actors
    movementActor.tell(new AddListenerEvent(movementPublisherSink), null)
    telemetryActor.tell(new AddListenerEvent(telemetryPublisherSink), null)
    visionActor.tell(new AddListenerEvent(visionPublisherSink), null)

  }

}
