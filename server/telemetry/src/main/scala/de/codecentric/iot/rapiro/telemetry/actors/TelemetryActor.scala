package de.codecentric.iot.rapiro.telemetry.actors

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.telemetry.actors.TelemetryActor.UpdateTelemetryData
import de.codecentric.iot.rapiro.telemetry.model.TelemetryData
import org.hyperic.sigar.{CpuPerc, Mem, Sigar}
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("telemetryActor")
class TelemetryActor extends Actor with InitializingBean {
  import context.dispatcher

  var listeners: List[ActorRef] = List[ActorRef]()
  var sigar: Sigar = new Sigar
  var pid: Long = sigar.getPid

  override def afterPropertiesSet(): Unit = {
    context.system.scheduler.schedule(5 seconds, 1 seconds, self, new UpdateEvent())
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:UpdateEvent =>
      if(listeners.nonEmpty) {
        val updateScene: UpdateTelemetryData = UpdateTelemetryData(getTelemetryData)
        listeners.foreach(target => target ! updateScene)
      }
  }

  def getTelemetryData: TelemetryData = {
    val cpuPerc: CpuPerc = sigar.getCpuPerc
    val mem: Mem = sigar.getMem
    val telemetryData: TelemetryData = new TelemetryData
    telemetryData.setCpuLoad(cpuPerc.getCombined)
    telemetryData.setMemoryUsage(mem.getUsed.toDouble / mem.getTotal.toDouble)
    telemetryData
  }

}

object TelemetryActor {
  case class UpdateTelemetryData(telemetryData: TelemetryData) {}
}



