package de.codecentric.iot.rapiro.movement.model;

/**
 * Created by christoferdutz on 03.05.16.
 */
public class MovementState {

    private int[] servoPositions;
    private int[] eyeColors;
    private int irDistance;

    public MovementState() {
    }

    public MovementState(int[] servoPositions, int[] eyeColors, int irDistance) {
        this.servoPositions = servoPositions;
        this.eyeColors = eyeColors;
        this.irDistance = irDistance;
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
