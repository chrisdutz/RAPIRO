package de.codecentric.iot.rapiro.blazeds.actors

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import de.codecentric.iot.rapiro.akka.ItemEvent
import flex.messaging.{Destination, MessageBroker}
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.flex.messaging.MessageTemplate

/**
  * Created by christoferdutz on 24.10.16.
  */
abstract class BlazeDsPublishingActor extends Actor with LazyLogging with InitializingBean {

  @Autowired private val broker: MessageBroker = null
  @Autowired private val template: MessageTemplate = null

  def destinationName:String

  override def receive: Receive = {
    case eventItem:ItemEvent[Object] =>
      try {
        template.send(destinationName, eventItem.getItem)
      } catch {
        case e: Exception => logger.error("An error occurred while processing incoming event {}", eventItem, e)
      }
  }

  override def afterPropertiesSet(): Unit = {
    val service = broker.getService("messaging-service")
    if (service.getDestination(destinationName) == null) {
      val destination: Destination = service.createDestination(destinationName)
      service.addDestination(destination)
      if (!service.isStarted) service.start()
      else if (!destination.isStarted) destination.start()
      logger.info("Initialized messaging destination: '{}'", destinationName)
    }
  }

}
