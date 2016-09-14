package de.codecentric.iot.rapiro.akka;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * Produces an actor.
 * It returns an instance of an actor bean, which is created by Spring.
 * If the scope of that bean is "prototype", a new actor instance is created,
 * in all other cases a singleton reference is returned.
 *
 * This class is noting else than a proxy to the spring application context.
 */
public class SpringActorProducer implements IndirectActorProducer {

    final private ApplicationContext applicationContext;
    final private String actorBeanName;

    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

    /**
     * @return reference to the spring actor bean.
     */
    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(actorBeanName);
    }

    /**
     * @return The type of the spring actor bean.
     */
    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }

}
