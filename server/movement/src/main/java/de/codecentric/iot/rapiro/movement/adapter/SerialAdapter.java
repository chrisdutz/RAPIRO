package de.codecentric.iot.rapiro.movement.adapter;

/**
 * Created by christoferdutz on 23.09.16.
 */
public interface SerialAdapter {

    int bytesAvailable();
    void send(String command);
    byte peekByte();
    byte readByte();
    int readWord();

    void addAsyncReader(AsyncReader asyncReader);

}
