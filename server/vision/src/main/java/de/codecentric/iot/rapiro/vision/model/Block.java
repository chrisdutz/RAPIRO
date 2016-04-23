package de.codecentric.iot.rapiro.vision.model;

/**
 * Created by christoferdutz on 23.04.16.
 */
public class Block {

    private int signature;
    private int x, y;
    private int width, height;

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
}
