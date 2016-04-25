package de.codecentric.iot.rapiro.vision.model;

/**
 * Created by christoferdutz on 23.04.16.
 */
public class ColorBlock extends Block {

    private int angle;

    public ColorBlock() {
        // Needed for BlazeDS
    }

    public ColorBlock(int signature, int x, int y, int width, int height, int angle) {
        super(signature, x, y, width, height);
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "ColorBlock{" +
                "signature=" + getSignature() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", angle=" + angle +
                '}';
    }
}
