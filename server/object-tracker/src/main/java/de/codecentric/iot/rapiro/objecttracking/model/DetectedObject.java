package de.codecentric.iot.rapiro.objecttracking.model;

import de.codecentric.iot.rapiro.vision.model.Block;

/**
 * Created by christoferdutz on 17.10.16.
 */
public class DetectedObject extends Block {

    private int id;

    public DetectedObject(Block block, int id) {
        this.setSignature(block.getSignature());
        this.setX(block.getX());
        this.setY(block.getY());
        this.setWidth(block.getWidth());
        this.setHeight(block.getHeight());
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
