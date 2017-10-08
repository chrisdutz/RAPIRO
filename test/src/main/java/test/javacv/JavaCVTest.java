package test.javacv;

import org.bytedeco.javacv.*;
import static org.bytedeco.javacpp.opencv_core.*;

public class JavaCVTest {
    public static void main(String[] args) throws Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.setImageWidth(640);
        grabber.setImageHeight(480);
        grabber.start();

        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();

        IplImage grabbedImage = grabberConverter.convert(grabber.grab());

        IplImage grayImage  = IplImage.create(grabbedImage.width(),   grabbedImage.height(),   IPL_DEPTH_8U, 1);

        System.out.println(grayImage.height());

        IplImage smallImage = IplImage.create(grabbedImage.width()/4, grabbedImage.height()/4, IPL_DEPTH_8U, 1);

        System.out.println(smallImage.height());

        grabber.stop();
        grabber.release();
    }
}