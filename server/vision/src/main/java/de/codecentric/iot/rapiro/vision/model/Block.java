package de.codecentric.iot.rapiro.vision.model;

import java.io.Serializable;

/**
 * Created by christoferdutz on 23.04.16.
 */
public class Block implements Serializable {

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

    public int getSurfaceSize() {
        return getWidth() * getHeight();
    }

    public boolean intersects(Block other) {
        return (signature == other.signature) &&
                (x < other.x + other.width) && (x + width > other.x) &&
                (y < other.y + other.height) && (y + height > other.y);
    }

    public Block unite(Block other) {
        int leftBound = Math.min(x - (width / 2), other.x - (other.width / 2));
        int rightBound = Math.max(x + (width / 2), other.x + (other.width / 2));
        int topBound = Math.min(y - (height / 2), other.y - (other.height / 2));
        int bottomBound = Math.max(y + (height / 2), other.y + (other.height / 2));
        int unityWidth = rightBound - leftBound;
        int unityHeight = bottomBound - topBound;
        int unityX = leftBound + (unityWidth / 2);
        int unityY = topBound + (unityHeight / 2);
        return new Block(signature, unityX, unityY, unityWidth, unityHeight);
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
