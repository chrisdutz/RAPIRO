package de.codecentric.iot.rapiro.vision.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by christoferdutz on 14.10.16.
 */
public class Scene implements Serializable {

    private Calendar time;

    private Set<Block> blocks;

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

}
