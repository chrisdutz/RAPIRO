package de.codecentric.iot.rapiro.movement.adapter;

/**
 * Created by christoferdutz on 23.09.16.
 */
public interface SerialAdapter {

    void send(String command);
    String read(int length);

}
