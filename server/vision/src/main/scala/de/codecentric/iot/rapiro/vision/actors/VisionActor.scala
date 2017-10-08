package de.codecentric.iot.rapiro.vision.actors

import java.io.File
import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.akka.ItemEvent
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.model.{Block, Scene}
import org.opencv.core._
import org.opencv.objdetect.CascadeClassifier
import org.opencv.videoio.VideoCapture
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.apache.commons.io.FileUtils
import org.opencv.imgproc.Imgproc
import org.springframework.core.env.Environment

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("visionActor")
class VisionActor() extends Actor with LazyLogging with InitializingBean {
  import context.dispatcher

  import org.springframework.beans.factory.annotation.Autowired

  @Autowired
  val env: Environment = null

  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
  private var camera:VideoCapture = _
  private var faceCascade:CascadeClassifier = _

  var running: Boolean = false

  var listeners:List[ActorRef] = List[ActorRef]()

  override def afterPropertiesSet(): Unit = {
    println(env)
    camera = new VideoCapture(0)
    if(!camera.isOpened){
      logger.info("Camera Error")
    }
    else{
      logger.info("Camera OK")

      var cascadeConfigPath:String = null
      val cascadeConfig = classOf[VisionActor].getResourceAsStream("/vision/lbpcascade_frontalface_improved.xml")
      if (cascadeConfig != null) {
        val cascadeConfigTempFile:File = File.createTempFile(String.valueOf(cascadeConfig.hashCode), ".xml")
        cascadeConfigTempFile.deleteOnExit()
        FileUtils.copyInputStreamToFile(cascadeConfig, cascadeConfigTempFile)
        cascadeConfigPath = cascadeConfigTempFile.getAbsolutePath
        logger.info("Found cascade config: " + cascadeConfigPath)
      }
      else {
        logger.error("Error retrieving cascade config")
        return
      }
      faceCascade = new CascadeClassifier(cascadeConfigPath)

      context.system.scheduler.schedule(5.seconds, 200.millis, self, new UpdateEvent())

      logger.info("Scheduled VisionActor")
    }
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:UpdateEvent =>
      if(!running) {
        try {
          running = true
          if (listeners.nonEmpty) {
            val scene: Scene = getScene
            val updateScene: UpdateScene = UpdateScene(scene)
            listeners.foreach(target => target ! updateScene)
          }
        } catch {
          case e: Exception => logger.error("An error occurred while processing incoming update event.", e)
        } finally {
          running = false
        }
      }
  }

  def getScene: Scene = {
    val frame = new Mat
    camera.read(frame)

    // If the mode is "rapiro" we have to flip the image as the camera is mounted upside down.
    if (env.getActiveProfiles.contains("raspberry")) Core.flip(frame, frame, -1)

    // Optimize the image to make processing easier.
    val optimized = new Mat
    Imgproc.cvtColor(frame, optimized, Imgproc.COLOR_BGR2GRAY, 1)
    Imgproc.equalizeHist(optimized, optimized)

    // Execute the face detection.
    val faceDetections:MatOfRect = new MatOfRect
    faceCascade.detectMultiScale(optimized, faceDetections)

    // Draw a bounding box around each face.
    val blocks:ArrayBuffer[Block] = ArrayBuffer[Block]()
    for (rect:Rect <- faceDetections.toArray) {
      val block:Block = new Block(0, optimized.width() - rect.x, rect.y, rect.width, rect.height)
      blocks += block
    }

    val scene: Scene = new Scene
    scene.setWidth(frame.width())
    scene.setHeight(frame.height())
    scene.setTime(Calendar.getInstance())
    scene.setBlocks(blocks.toArray)

    scene
  }

}

object VisionActor {
  case class UpdateScene(scene:Scene) extends ItemEvent[Scene] {
    override def getItem: Scene = scene
  }
}
