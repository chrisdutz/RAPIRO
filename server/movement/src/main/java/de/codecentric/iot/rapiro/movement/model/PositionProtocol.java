package de.codecentric.iot.rapiro.movement.model;

/**
 * Created by christoferdutz on 14.09.16.
 */
public class PositionProtocol {

    final public static class Position {

        private int[] servoPositions;
        private int[] eyeColors;
        private int irDistance;

        public Position() {
        }

        public Position(int[] servoPositions, int[] eyeColors, int irDistance) {
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

    public static class UpdatePositionAcceptedMessage {
        @Override
        public String toString() {
            return "UpdatePositionAccepted";
        }
    }
    public static final UpdatePositionAcceptedMessage UpdatePositionAccepted = new UpdatePositionAcceptedMessage();

    public static class UpdatePositionDeniedMessage {
        @Override
        public String toString() {
            return "UpdatePositionDenied";
        }
    }
    public static final UpdatePositionDeniedMessage UpdatePositionDenied = new UpdatePositionDeniedMessage();

}
