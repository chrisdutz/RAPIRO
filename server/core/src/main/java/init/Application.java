package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by christoferdutz on 21.03.16.
 */

@SpringBootApplication
@ComponentScan("de.codecentric.iot.rapiro")
public class Application {

    /**
     * Load the mraajava library used to access Edison hardware I/O.
     */
    static {
        try {
            System.loadLibrary("mraajava");
            System.setProperty("mraa-loaded", "true");
        } catch (UnsatisfiedLinkError e) {
            //System.err.println("Native code library failed to load.");
            System.setProperty("mraa-loaded", "false");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
