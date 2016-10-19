package de.codecentric.iot.rapiro.movement.model;

import java.util.Calendar;

/**
 * Created by christoferdutz on 21.09.16.
 */
public class Position {

    private Calendar time;
    private int[] servoPositions;
    private int[] eyeColors;
    private int irDistance;

    public Position() {
    }

    public Position(Calendar time, int[] servoPositions, int[] eyeColors, int irDistance) {
        this.time = time;
        this.servoPositions = servoPositions;
        this.eyeColors = eyeColors;
        this.irDistance = irDistance;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public int[] getServoPositions() {
        return servoPositions;
    }

    public void setServoPositions(int[] servoPositions) {
        this.servoPositions = servoPositions;
    }

    public int[] getEyeColors() {
        return eyeColors;
    }

    public void setEyeColors(int[] eyeColors) {
        this.eyeColors = eyeColors;
    }

    public int getIrDistance() {
        return irDistance;
    }

    public void setIrDistance(int irDistance) {
        this.irDistance = irDistance;
    }

}
