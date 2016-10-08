package init;

import com.pi4j.io.serial.*;
import com.pi4j.io.serial.impl.SerialImpl;
import com.pi4j.util.NativeLibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * Created by christoferdutz on 21.03.16.
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan("de.codecentric.iot.rapiro")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        String platformProfile;
        try {
            Runtime.getRuntime().loadLibrary("mraajava");
            platformProfile = "edison";
        } catch (UnsatisfiedLinkError ee) {
            try {
                //Runtime.getRuntime().loadLibrary("libpi4j");
                NativeLibraryLoader.load("libpi4j.so");
                // TODO: the above command swallows the exception, so we have to do something else.
                new SerialImpl().open(Serial.DEFAULT_COM_PORT, Baud._57600, DataBits._8, Parity.NONE,
                        StopBits._1, FlowControl.NONE);
                platformProfile = "raspberry";
            } catch(UnsatisfiedLinkError | IOException er) {
                platformProfile = "dummy";
            }
        }
        LOG.info("-----------------------------------------------");
        LOG.info("Detected platform: " + platformProfile);
        LOG.info("-----------------------------------------------");

        SpringApplication application = new SpringApplication(Application.class);
        application.setAdditionalProfiles(platformProfile);
        application.run(args);
    }

}
