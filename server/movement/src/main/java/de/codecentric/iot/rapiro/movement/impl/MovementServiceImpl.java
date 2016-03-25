package de.codecentric.iot.rapiro.movement.impl;

import de.codecentric.iot.rapiro.movement.MovementService;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("movementService")
@RemotingDestination
public class MovementServiceImpl implements MovementService {

    public MovementServiceImpl() {
        System.out.println("Create");
    }

    @Override
    public void stop() {
        System.out.println("Stop");
    }

    @Override
    public void moveForward() {
        System.out.println("Forward");
    }

    @Override
    public void moveLeft() {
        System.out.println("Left");
    }

    @Override
    public void moveRight() {
        System.out.println("Right");
    }

    @Override
    public void moveBack() {
        System.out.println("Back");
    }

}
