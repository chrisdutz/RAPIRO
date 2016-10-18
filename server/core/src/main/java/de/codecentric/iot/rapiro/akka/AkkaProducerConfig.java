package de.codecentric.iot.rapiro.akka;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import de.codecentric.iot.rapiro.akka.consumer.Consumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Created by christoferdutz on 17.10.16.
 */
public abstract class AkkaProducerConfig<T> implements InitializingBean {

    final Class<T> typeParameterClass;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired(required = false)
    private List<Consumer<T>> consumers;

    public AkkaProducerConfig(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if((consumers != null) && !consumers.isEmpty()) {
            // The Materializer will take a flow description and create the resources it needs
            // to operate. As we are building an actor based system, the ActorMaterializer
            // handles creation of the actors themselves.
            final Materializer materializer = ActorMaterializer.create(actorSystem);

            // Create a producer actor instance
            Source producer = Source.actorPublisher(
                    SpringExtension.SpringExtProvider.get(actorSystem).props(getActorName()));

            RunnableGraph<Source<String, NotUsed>> runnableGraph =
                    producer.toMat(BroadcastHub.of(typeParameterClass, 256), Keep.right());

            Source<String, NotUsed> fromProducer = runnableGraph.run(materializer);

//            fromProducer.runForeach()

        }
    }

    protected abstract String getActorName();

}
