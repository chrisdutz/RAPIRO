package de.codecentric.iot.rapiro.vision.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by christoferdutz on 14.10.16.
 */
public class Scene implements Serializable {

    private Set<Block> blocks;

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

}
