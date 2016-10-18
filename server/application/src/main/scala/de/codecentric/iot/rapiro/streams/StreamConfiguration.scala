package de.codecentric.iot.rapiro.streams

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.impl.StreamLayout.CompositeModule
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import de.codecentric.iot.rapiro.akka.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class StreamConfiguration {

  @Autowired private val actorSystem: ActorSystem = null

  @Autowired private implicit val materializer: Materializer = null

  @Bean
  def visionStream(): RunnableGraph[CompositeModule] = {
    // Get a reference to the visionActor, which is the source of all scene elements.
    val visionActor = Source.actorPublisher(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionActor")
    )

    // Get a reference to the objectTracker, which will consume Scene events produced
    // by the visionActor and detect objects and their movement.
    /*val objectTracker = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("objectTrackerActor")
    )*/
    val publish = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("visionPublishingActor")
    )

    // Connect the visionActor with the objectTracker
    val flow = visionActor to publish

    // Start the stream of events.
    flow.run()

    // We have to return something ...
    null
  }

  @Bean
  def movementStream(): RunnableGraph[CompositeModule] = {
    // Get a reference to the visionActor, which is the source of all scene elements.
    val telemetryActor = Source.actorPublisher(
      SpringExtension.SpringExtProvider.get(actorSystem).props("positionActor")
    )

    val publish = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("movementPublishingActor")
    )

    val flow = telemetryActor to publish

    flow.run()

    null
  }

  @Bean
  def telemetryStream(): RunnableGraph[CompositeModule] = {
    // Get a reference to the visionActor, which is the source of all scene elements.
    val telemetryActor = Source.actorPublisher(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryActor")
    )

    val publish = Sink.actorSubscriber(
      SpringExtension.SpringExtProvider.get(actorSystem).props("telemetryPublishingActor")
    )

    val flow = telemetryActor to publish

    flow.run()

    null
  }

}
