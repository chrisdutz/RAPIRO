package de.codecentric.iot.rapiro.vision.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by christoferdutz on 14.10.16.
 */
public class Scene implements Serializable {

    private Calendar time;

    private Block[] blocks;

    private int width;
    private int height;

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
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
}
