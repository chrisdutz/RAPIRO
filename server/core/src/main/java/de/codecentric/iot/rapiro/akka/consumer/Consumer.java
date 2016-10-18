package de.codecentric.iot.rapiro.akka.consumer;

/**
 * Created by christoferdutz on 17.10.16.
 */
public interface Consumer<T> {

    void handle(T msg);

}
