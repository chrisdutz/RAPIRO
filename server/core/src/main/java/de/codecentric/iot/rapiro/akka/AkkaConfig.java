package de.codecentric.iot.rapiro.akka;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by christoferdutz on 14.09.16.
 */
@Configuration
public class AkkaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AkkaConfig.class);

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
        LOG.info("-----------------------------------------------");
        LOG.info("Initializing Akka system");
        LOG.info("-----------------------------------------------");

        ActorSystem system = ActorSystem.create("AkkaJavaSpring");
        // initialize the application context in the Akka Spring Extension
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);

        LOG.info("Akka system initialized");

        return system;
    }

    @Bean
    public Materializer materializer(ActorSystem actorSystem) {
        return ActorMaterializer.create(actorSystem);
    }

}
