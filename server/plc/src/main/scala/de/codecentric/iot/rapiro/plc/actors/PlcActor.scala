package de.codecentric.iot.rapiro.plc.actors

import Moka7._
import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging
import de.codecentric.iot.rapiro.akka.ItemEvent
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.plc.actors.PlcActor.UpdatePlcData
import de.codecentric.iot.rapiro.plc.events.{ConnectEvent, DisconnectEvent}
import de.codecentric.iot.rapiro.plc.model.PlcData
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("plcActor")
class PlcActor extends Actor with LazyLogging with InitializingBean {

  import context.dispatcher

  val rack:Int = 0
  val slot:Int = 0
  val addr:String = "192.168.0.1"

  val client: S7Client = new S7Client
  var connected:Boolean = false

  var listeners: List[ActorRef] = List[ActorRef]()

  override def afterPropertiesSet(): Unit = {
    client.SetConnectionType(S7.OP)

    context.system.scheduler.schedule(5.seconds, 200.millisecond, self, new UpdateEvent())

    logger.info("Scheduled PlcActor")
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:ConnectEvent =>
      if(!connected) {
        connect()
      }
    case _:DisconnectEvent =>
      if(connected) {
        disconnect()
      }
    case _:UpdateEvent =>
      try {
        //if (listeners.nonEmpty) {
        val plcData: PlcData = getPlcData
        if (plcData != null) {
          val updateData: UpdatePlcData = UpdatePlcData(plcData)
          listeners.foreach(target => target ! updateData)
        }
        //}
      } catch {
        case e: Exception => logger.error("An error occurred while processing incoming update event.", e)
      }
  }

  def connect(): Unit = {
    client.Disconnect()
    var result = client.ConnectTo(addr, rack, slot)
    if (result == 0) {
      logger.info("Connected to   : " + addr + " (Rack=" + rack + ", Slot=" + slot + ")")
      logger.info("PDU negotiated : " + client.PDULength + " bytes")

      val orderCode = new S7OrderCode
      result = client.GetOrderCode(orderCode)
      if (result == 0) {
        logger.info("Order Code        : " + orderCode.Code)
        logger.info("Firmware version  : " + orderCode.V1 + "." + orderCode.V2 + "." + orderCode.V3)

        val cpInfo = new S7CpInfo
        result = client.GetCpInfo(cpInfo)
        if (result == 0) {
          logger.info("Max PDU Length    : " + cpInfo.MaxPduLength)
          logger.info("Max connections   : " + cpInfo.MaxConnections)
          logger.info("Max MPI rate (bps): " + cpInfo.MaxMpiRate)
          logger.info("Max Bus rate (bps): " + cpInfo.MaxBusRate)

          connected = true
        }
      }
    }
  }

  def disconnect(): Unit = {
    connected = false
  }

  def getPlcData: PlcData = {
    if(!connected) {
      connect()
    }
    val data:PlcData = new PlcData
    data.setConnected(connected)
    if(connected) {
      // Read the input area
      val inputBuffer: Array[Byte] = new Array[Byte](65536) // 64K buffer (maximum for S7400 systems)
      var result = client.ReadArea(S7.S7AreaPE, 0, 0, 1, inputBuffer)
      if (result != 0) {
        logger.error("Error reading PLC data. Got response {}", result)
        connected = false
        return null
      }

      // Read the output area
      val outputBuffer: Array[Byte] = new Array[Byte](65536) // 64K buffer (maximum for S7400 systems)
      result = client.ReadArea(S7.S7AreaPA, 0, 0, 1, outputBuffer)
      if (result != 0) {
        logger.error("Error reading PLC data. Got response {}", result)
        connected = false
        return null
      }

      data.setInput(inputBuffer.apply(0))
      data.setOutput(outputBuffer.apply(0))
    }
    data
  }

}

object PlcActor {
  case class UpdatePlcData(plcData: PlcData) extends ItemEvent[PlcData] {
    override def getItem: PlcData = plcData
  }
}



