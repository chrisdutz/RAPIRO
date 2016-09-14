package de.codecentric.iot.rapiro.akka;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by christoferdutz on 14.09.16.
 */
@Configuration
public class AkkaConfig {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Initialize the akka system, which will be used througout the application
     * Additionally initialize the SpringExtension, which is a little helper that
     * helps create spring-enabled actors.
     *
     * @return an instance of the Akka actor system.
     */
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("AkkaJavaSpring");
        // initialize the application context in the Akka Spring Extension
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }

}
