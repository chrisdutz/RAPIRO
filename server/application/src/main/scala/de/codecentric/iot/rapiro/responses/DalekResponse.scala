package de.codecentric.iot.rapiro.responses

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import de.codecentric.iot.rapiro.plc.actors.PlcActor.UpdatePlcData
import de.codecentric.iot.rapiro.plc.model.PlcData
import de.codecentric.iot.rapiro.voice.VoiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by christoferdutz on 07.10.17.
  */
@Scope("prototype")
@Component("dalekResponse")
class DalekResponse extends Actor with LazyLogging {

  @Autowired var voiceService: VoiceService = _

  var plcData: PlcData = _

  override def receive: Receive = {
    case event: UpdatePlcData =>
      if((event.getItem != null) && (plcData != null) && (event.getItem.getOutput == 1) && (plcData.getOutput == 0)) {
        voiceService.play("dalek.wav")
      }
      plcData = event.getItem
  }

}
