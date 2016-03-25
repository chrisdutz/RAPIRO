package de.codecentric.iot.rapiro.movement;

/**
 * Created by christoferdutz on 23.03.16.
 */
public interface MovementService {

    void stop();
    void moveForward();
    void moveLeft();
    void moveRight();
    void moveBack();

}
