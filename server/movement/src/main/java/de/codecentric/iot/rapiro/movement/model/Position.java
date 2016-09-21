package de.codecentric.iot.rapiro.movement.model;

/**
 * Created by christoferdutz on 21.09.16.
 */
public class Position {

    private long timestamp;
    private int[] servoPositions;
    private int[] eyeColors;
    private int irDistance;

    public Position() {
    }

    public Position(long timestamp, int[] servoPositions, int[] eyeColors, int irDistance) {
        this.timestamp = timestamp;
        this.servoPositions = servoPositions;
        this.eyeColors = eyeColors;
        this.irDistance = irDistance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
