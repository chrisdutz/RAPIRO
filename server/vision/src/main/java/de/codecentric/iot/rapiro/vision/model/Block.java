package de.codecentric.iot.rapiro.vision.model;

/**
 * Created by christoferdutz on 23.04.16.
 */
public class Block {

    private int signature;
    private int x, y;
    private int width, height;

    public Block() {
        // Needed for BlazeDS
    }

    public Block(int signature, int x, int y, int width, int height) {
        this.signature = signature;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Block{" +
                "signature=" + signature +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public int getChecksum() {
        return getSignature() + getX() + getY() + getWidth() + getHeight();
    }

}
