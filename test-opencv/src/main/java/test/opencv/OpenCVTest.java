package test.opencv;

import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.InputStream;

public class OpenCVTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        VideoCapture camera = new VideoCapture(0);
        camera.open(0); //Useless
        if(!camera.isOpened()){
            System.out.println("Camera Error");
            return;
        }
        else{
            System.out.println("Camera OK");
        }

        String cascadeConfigPath;
        InputStream cascadeConfig = OpenCVTest.class.getResourceAsStream("/lbpcascade_frontalface_improved.xml");
        if(cascadeConfig != null) {
            File cascadeConfigTempFile = File.createTempFile(String.valueOf(cascadeConfig.hashCode()), ".xml");
            cascadeConfigTempFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(cascadeConfig, cascadeConfigTempFile);
            cascadeConfigPath = cascadeConfigTempFile.getAbsolutePath();
        } else {
            System.out.println("Error retrieving cascade config");
            return;
        }

        // Create a face detector from the cascade file in the resources directory.
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeConfigPath);

        while(true) {
            Mat frame = new Mat();
            camera.read(frame);

            if(frame.empty()) {
                System.out.println("Empty Frame");
                continue;
            }

            if(true) {
                Core.flip(frame, frame, -1);
            }

            // Optimize the image to make processing easier.
            Mat optimized = new Mat();
            Imgproc.cvtColor(frame, optimized, Imgproc.COLOR_BGR2GRAY, 1);
            Imgproc.equalizeHist(optimized, optimized);

            // Execute the face detection.
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(optimized, faceDetections);
            System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

            // Draw a bounding box around each face.
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(frame,
                        new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }

            // Save the visualized detection.
            String filename = "faceDetection.png";
            Imgcodecs.imwrite(filename, frame);
        }
    }
}
