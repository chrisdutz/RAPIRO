package de.codecentric.iot.rapiro.blazeds;

import flex.messaging.MessageBroker;
import flex.messaging.io.SerializationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.flex.messaging.MessageTemplate;

import javax.annotation.PostConstruct;

/**
 * Little helper to provide utils for BlazeDS.
 */
@Configuration
public class BlazeDsConfig {

    @Bean
    public MessageTemplate messageTemplate(BeanFactory beanFactory, MessageBroker messageBroker) {
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setBeanFactory(beanFactory);
        messageTemplate.setMessageBroker(messageBroker);
        return messageTemplate;
    }

    //@PostConstruct
    public void configureSerializationContext() {
        //ThreadLocal SerializationContent
        SerializationContext serializationContext = SerializationContext.getSerializationContext();
        serializationContext.enableSmallMessages = true;
        serializationContext.instantiateTypes = true;
        //use _remoteClass field
        serializationContext.supportRemoteClass = true;
        //false  Legacy Flex 1.5 behavior was to return a java.util.Collection for Array
        //true New Flex 2+ behavior is to return Object[] for AS3 Array
        serializationContext.legacyCollection = false;

        serializationContext.legacyMap = false;
        //false Legacy flash.xml.XMLDocument Type
        //true New E4X XML Type
        serializationContext.legacyXMLDocument = false;

        //determines whether the constructed Document is name-space aware
        serializationContext.legacyXMLNamespaces = false;
        serializationContext.legacyThrowable = false;
        serializationContext.legacyBigNumbers = false;

        serializationContext.restoreReferences = false;
        serializationContext.logPropertyErrors = false;
        serializationContext.ignorePropertyErrors = true;
    }

}
