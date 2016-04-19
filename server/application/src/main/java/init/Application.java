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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
