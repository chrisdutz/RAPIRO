package init;

import de.codecentric.iot.rapiro.movement.MovementConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by christoferdutz on 21.03.16.
 */

@EnableScheduling
@SpringBootApplication
@ComponentScan("de.codecentric.iot.rapiro")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(new Object[] {Application.class, MovementConfig.class}, args);
    }

}
