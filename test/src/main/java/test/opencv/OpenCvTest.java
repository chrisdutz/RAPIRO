package test.opencv;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class OpenCvTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        VideoCapture camera = new VideoCapture(0);
        Thread.sleep(1000);
        camera.open(0); //Useless
        if(!camera.isOpened()){
            System.out.println("Camera Error");
        }
        else{
            System.out.println("Camera OK?");
        }

        Mat frame = new Mat();

        //camera.grab();
        //System.out.println("Frame Grabbed");
        //camera.retrieve(frame);
        //System.out.println("Frame Decoded");
        while(true) {
            camera.read(frame);
            System.out.println("Frame Obtained");

            System.out.println("Captured Frame Width " + frame.width());

            System.out.println("\nRunning DetectFaceDemo");

            // Create a face detector from the cascade file in the resources
            // directory.
            CascadeClassifier faceDetector = new CascadeClassifier(OpenCvTest.class.getResource("/lbpcascade_frontalface_improved.xml").getPath());

            // Detect faces in the image.
            // MatOfRect is a special container class for Rect.
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(frame, faceDetections);

            System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

            // Draw a bounding box around each face.
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            }

            // Save the visualized detection.
            String filename = "faceDetection.png";
            System.out.println(String.format("Writing %s", filename));
            Imgcodecs.imwrite(filename, frame);

            Thread.sleep(500);
        }
    }

}
